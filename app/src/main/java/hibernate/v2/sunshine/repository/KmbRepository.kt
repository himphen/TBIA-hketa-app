package hibernate.v2.sunshine.repository

import com.fonfon.kgeohash.GeoHash
import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class KmbRepository(
    private val kmbDao: KmbDao,
    private val apiManager: ApiManager
) : BaseRepository() {

    suspend fun saveData() {
        val result = ApiSafeCall { apiManager.dataService.getKmbData() }

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
                            .map(KmbRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(KmbRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("KmbRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { kmbRouteStop ->
                                KmbRouteStopEntity.fromApiModel(kmbRouteStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("KmbRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { kmbStop ->
                                KmbStopEntity.fromApiModel(kmbStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("KmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    suspend fun getStopListDb(list: List<GeoHash>) = kmbDao.getStopList(list.map { it.toString() })
    suspend fun getRouteListDb() = kmbDao.getRouteList()
    suspend fun getRouteStopComponentListDb() = kmbDao.getRouteStopComponentList()
    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = kmbDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value,
        route.serviceType
    )

    suspend fun setMapRouteListIntoMapStop(stopList: List<SearchMapStop>): List<SearchMapStop> {
        return stopList.map {
            if (it.mapRouteList.isEmpty()) {
                it.mapRouteList = getRouteEtaCardList(it)
            }
            it
        }
    }

    suspend fun getRouteEtaCardList(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = kmbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList =
            kmbDao.getRouteListFromRouteId(routeStopList).filter { !it.isSpecialRoute() }
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

    suspend fun getStopDb(stop: String): KmbStopEntity? = kmbDao.getStop(stop).firstOrNull()

    suspend fun getRouteDb(
        route: String,
        bound: String,
        serviceType: String,
    ): KmbRouteEntity? = kmbDao.getRoute(
        route,
        bound,
        serviceType,
    )

    suspend fun initDatabase() {
        kmbDao.clearRouteList()
        kmbDao.clearStopList()
        kmbDao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<KmbRouteEntity>) {
        kmbDao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<KmbRouteStopEntity>) {
        kmbDao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<KmbStopEntity>) {
        kmbDao.addStopList(entityList)
    }
}
