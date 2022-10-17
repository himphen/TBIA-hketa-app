package hibernate.v2.sunshine.repository

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.service.DataService
import hibernate.v2.sunshine.db.lrt.LRTDao
import hibernate.v2.sunshine.db.lrt.LRTRouteEntity
import hibernate.v2.sunshine.db.lrt.LRTRouteStopEntity
import hibernate.v2.sunshine.db.lrt.LRTStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.route.TransportRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class LRTRepository(
    private val lrtDao: LRTDao
) : BaseRepository() {

    suspend fun saveData() {
        val result = ApiSafeCall { DataService.getLrtData() }

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
                            .map(LRTRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(LRTRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("LRTRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { routeStop ->
                                LRTRouteStopEntity.fromApiModel(routeStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("LRTRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { stop ->
                                LRTStopEntity.fromApiModel(stop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("LRTRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    suspend fun getRouteListDb() = lrtDao.getRouteList()
    suspend fun getRouteEnabledListDb() = lrtDao.getRouteList(true)
    suspend fun getRouteStopComponentListDb() =
        lrtDao.getRouteStopComponentList()

    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = lrtDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value,
        route.serviceType
    )

    suspend fun initDatabase() {
        lrtDao.clearRouteList()
        lrtDao.clearStopList()
        lrtDao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<LRTRouteEntity>) {
        lrtDao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<LRTRouteStopEntity>) {
        lrtDao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<LRTStopEntity>) {
        lrtDao.addStopList(entityList)
    }

    suspend fun getStopListDb() = lrtDao.getStopList()

    suspend fun getRouteListFromStopId(stopId: String): List<TransportRoute> {
        val routeStopList = lrtDao.getRouteStopListFromStopId(stopId)
        val routeList = lrtDao.getRouteListFromRouteId(routeStopList)

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
        val routeStopList = lrtDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = lrtDao.getRouteListFromRouteId(routeStopList)
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
