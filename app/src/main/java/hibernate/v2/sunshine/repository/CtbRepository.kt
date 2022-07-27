package hibernate.v2.sunshine.repository

import com.fonfon.kgeohash.GeoHash
import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.ctb.CtbDao
import hibernate.v2.sunshine.db.ctb.CtbRouteEntity
import hibernate.v2.sunshine.db.ctb.CtbRouteStopEntity
import hibernate.v2.sunshine.db.ctb.CtbStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class CtbRepository(
    private val ctbDao: CtbDao,
    private val apiManager: ApiManager
) : BaseRepository() {

    suspend fun saveData() {
        val result = ApiSafeCall { apiManager.dataService.getCtbData() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    data.route?.let { list ->
                        val temp = list
                            .map(CtbRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(CtbRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("NCRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { ctbRouteStop ->
                                CtbRouteStopEntity.fromApiModel(ctbRouteStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("NCRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { ctbStop ->
                                CtbStopEntity.fromApiModel(ctbStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("NCRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    suspend fun getRouteListByCompanyDb(company: Company) = ctbDao.getRouteList(company.value)
    suspend fun getRouteStopComponentListDb(company: Company) =
        ctbDao.getRouteStopComponentList(company.value)

    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = ctbDao.getRouteStopComponentList(
        route.company.value,
        route.routeId,
        route.bound.value
    )

    suspend fun initDatabase() {
        ctbDao.clearRouteList()
        ctbDao.clearStopList()
        ctbDao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<CtbRouteEntity>) {
        ctbDao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<CtbRouteStopEntity>) {
        ctbDao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<CtbStopEntity>) {
        ctbDao.addStopList(entityList)
    }

    suspend fun getStopListDb(list: List<GeoHash>) = ctbDao.getStopList(list.map { it.toString() })

    suspend fun getRouteListFromStopId(stopId: String): List<TransportRoute> {
        val routeStopList = ctbDao.getRouteStopListFromStopId(stopId)
        val routeList = ctbDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }

    suspend fun setMapRouteListIntoMapStop(stopList: List<SearchMapStop>): List<SearchMapStop> {
        return stopList.map {
            if (it.mapRouteList.isEmpty()) {
                it.mapRouteList = getRouteEtaCardList(it)
            }
            it
        }
    }

    suspend fun getRouteEtaCardList(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = ctbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = ctbDao.getRouteListFromRouteId(routeStopList)
        val routeHashMap = routeList.map {
            it.routeHashId() to it
        }.toMap()

        return routeStopList.mapNotNull {
            val route = routeHashMap[it.routeHashId()] ?: return@mapNotNull null

            Card.EtaCard(
                route.toTransportModel(),
                stop.toTransportModelWithSeq(it.seq),
                position = 0
            )
        }
    }
}
