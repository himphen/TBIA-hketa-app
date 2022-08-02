package hibernate.v2.sunshine.repository

import com.fonfon.kgeohash.GeoHash
import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.gmb.GmbDao
import hibernate.v2.sunshine.db.gmb.GmbRouteEntity
import hibernate.v2.sunshine.db.gmb.GmbRouteStopEntity
import hibernate.v2.sunshine.db.gmb.GmbStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.route.TransportRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class GmbRepository(
    private val gmbDao: GmbDao,
    private val apiManager: ApiManager
) : BaseRepository() {

    suspend fun saveData() {
        val result = ApiSafeCall { apiManager.dataService.getGmbData() }

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
                            .map(GmbRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(GmbRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("GmbRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { gmbRouteStop ->
                                GmbRouteStopEntity.fromApiModel(gmbRouteStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("GmbRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { gmbStop ->
                                GmbStopEntity.fromApiModel(gmbStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("GmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    suspend fun getRouteListDb(region: GmbRegion) = gmbDao.getRouteList(region.value)
    suspend fun getRouteStopComponentListDb(routeIdList: List<String>) =
        gmbDao.getRouteStopComponentList(routeIdList)

    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = gmbDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value
    )

    suspend fun initDatabase() {
        gmbDao.clearRouteList()
        gmbDao.clearStopList()
        gmbDao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<GmbRouteEntity>) {
        gmbDao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<GmbRouteStopEntity>) {
        gmbDao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<GmbStopEntity>) {
        gmbDao.addStopList(entityList)
    }

    suspend fun getStopListDb(list: List<GeoHash>) = gmbDao.getStopList(list.map { it.toString() })

    suspend fun getRouteListFromStopId(stopId: String): List<TransportRoute> {
        val routeStopList = gmbDao.getRouteStopListFromStopId(stopId)
        val routeList = gmbDao.getRouteListFromRouteId(routeStopList)

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
        val routeStopList = gmbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = gmbDao.getRouteListFromRouteId(routeStopList)
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
