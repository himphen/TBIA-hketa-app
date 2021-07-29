package hibernate.v2.sunshine.repository

import hibernate.v2.api.response.kmb.KmbRouteListResponse
import hibernate.v2.api.response.kmb.KmbRouteStopListResponse
import hibernate.v2.api.response.kmb.KmbStopListResponse
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute

class KmbRepository(
    private val apiManager: ApiManager,
    private val kmbDao: KmbDao,
) {

    suspend fun getStopListDb() = kmbDao.getStopList()
    suspend fun getRouteListDb() = kmbDao.getRouteList()
    suspend fun getRouteStopComponentListDb() = kmbDao.getRouteStopComponentList()

    suspend fun getRouteListFromStopId(stopId: String): List<TransportRoute> {
        val routeStopList = kmbDao.getRouteStopListFromStopId(stopId)
        val routeList = kmbDao.getRouteListFromRouteId(routeStopList)

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
        val routeStopList = kmbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = kmbDao.getRouteListFromRouteId(routeStopList)
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

//    suspend fun getRouteStopComponentList(
//        routeId: String,
//        bound: Bound,
//        serviceType: String,
//    ) = kmbDao.getRouteStopComponentList(
//        routeId = routeId,
//        bound = bound,
//        serviceType = serviceType
//    )

    suspend fun hasStopListDb() = kmbDao.getSingleStop() != null
    suspend fun hasRouteListDb() = kmbDao.getSingleRoute() != null
    suspend fun hasRouteStopListDb() = kmbDao.getSingleRouteStop() != null

    suspend fun isDataExisted() = hasStopListDb() && hasRouteListDb() && hasRouteStopListDb()

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

    suspend fun getRouteListApi(): KmbRouteListResponse {
        return apiManager.kmbService.getRouteList()
    }

    suspend fun getStopListApi(): KmbStopListResponse {
        return apiManager.kmbService.getStopList()
    }

    suspend fun getRouteStopListApi(): KmbRouteStopListResponse {
        return apiManager.kmbService.getRouteStopList()
    }

    suspend fun initDatabase() {
        kmbDao.clearRouteList()
        kmbDao.clearStopList()
        kmbDao.clearRouteStopList()
    }

    suspend fun saveRouteListApi() {
        val list = getRouteListApi().routeList.map { route ->
            KmbRouteEntity.fromApiModel(route)
        }

        kmbDao.addRouteList(list)
    }

    suspend fun saveStopListApi() {
        val list = getStopListApi().stopList.map { stop ->
            KmbStopEntity.fromApiModel(stop)
        }

        kmbDao.addStopList(list)
    }

    suspend fun saveRouteStopListApi() {
        val list = getRouteStopListApi().routeStopList.map { routeStop ->
            KmbRouteStopEntity.fromApiModel(routeStop)
        }

        kmbDao.addRouteStopList(list)
    }
}
