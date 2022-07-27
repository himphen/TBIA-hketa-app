package hibernate.v2.sunshine.repository

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.mtr.MTRDao
import hibernate.v2.sunshine.db.mtr.MTRRouteEntity
import hibernate.v2.sunshine.db.mtr.MTRRouteStopEntity
import hibernate.v2.sunshine.db.mtr.MTRStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class MTRRepository(
    private val mtrDao: MTRDao,
    private val apiManager: ApiManager
) : BaseRepository() {

    suspend fun saveData() {
        val result = ApiSafeCall { apiManager.dataService.getMtrData() }

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
                            .map(MTRRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(MTRRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("MTRRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { routeStop ->
                                MTRRouteStopEntity.fromApiModel(routeStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("MTRRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { stop ->
                                MTRStopEntity.fromApiModel(stop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("MTRRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    suspend fun getRouteListDb() = mtrDao.getRouteList()
    suspend fun getRouteEnabledListDb() = mtrDao.getRouteList(true)
    suspend fun getRouteStopComponentListDb() =
        mtrDao.getRouteStopComponentList()

    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = mtrDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value,
        route.serviceType
    )

    suspend fun initDatabase() {
        mtrDao.clearRouteList()
        mtrDao.clearStopList()
        mtrDao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<MTRRouteEntity>) {
        mtrDao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<MTRRouteStopEntity>) {
        mtrDao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<MTRStopEntity>) {
        mtrDao.addStopList(entityList)
    }

    suspend fun getStopListDb() = mtrDao.getStopList()

    suspend fun getRouteListFromStopId(stopId: String): List<TransportRoute> {
        val routeStopList = mtrDao.getRouteStopListFromStopId(stopId)
        val routeList = mtrDao.getRouteListFromRouteId(routeStopList)

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
        val routeStopList = mtrDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = mtrDao.getRouteListFromRouteId(routeStopList)
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
