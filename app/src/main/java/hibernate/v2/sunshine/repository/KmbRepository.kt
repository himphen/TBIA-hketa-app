package hibernate.v2.sunshine.repository

import hibernate.v2.api.response.RouteListResponse
import hibernate.v2.api.response.RouteStopListResponse
import hibernate.v2.api.response.StopListResponse
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KmbRepository(
    private val apiManager: ApiManager,
    private val kmbDao: KmbDao,
) {

    suspend fun getStopListDb() = kmbDao.getStopList()
    suspend fun getRouteListDb() = withContext(Dispatchers.IO) { kmbDao.getRouteList() }
    suspend fun getRouteStopDetailsListDb() =
        withContext(Dispatchers.IO) { kmbDao.getRouteStopDetailsList() }

    suspend fun getRouteStopListDb() = kmbDao.getRouteStopList()
//    suspend fun getRouteStopDetailsList(
//        routeId: String,
//        bound: Bound,
//        serviceType: String,
//    ) = kmbDao.getRouteStopDetailsList(
//        routeId = routeId,
//        bound = bound,
//        serviceType = serviceType
//    )

    suspend fun hasStopListDb() = kmbDao.getSingleStop() != null
    suspend fun hasRouteListDb() = kmbDao.getSingleRoute() != null
    suspend fun hasRouteStopListDb() = kmbDao.getSingleRouteStop() != null

    suspend fun isDataExisted() =
        withContext(Dispatchers.IO) { hasStopListDb() && hasRouteListDb() && hasRouteStopListDb() }

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

    suspend fun getRouteListApi(): RouteListResponse {
        return apiManager.etaService.getRouteList()
    }

    suspend fun getStopListApi(): StopListResponse {
        return apiManager.etaService.getStopList()
    }

    suspend fun getRouteStopListApi(): RouteStopListResponse {
        return apiManager.etaService.getRouteStopList()
    }

    suspend fun saveRouteListApi() {
        val list = getRouteListApi().routeList.map { route ->
            KmbRouteEntity.fromApiModel(route)
        }

        kmbDao.clearRouteList()
        kmbDao.addRouteList(list)
    }

    suspend fun saveStopListApi() {
        val list = getStopListApi().stopList.map { stop ->
            KmbStopEntity.fromApiModel(stop)
        }

        kmbDao.clearStopList()
        kmbDao.addStopList(list)
    }

    suspend fun saveRouteStopListApi() {
        val list = getRouteStopListApi().routeStopList.map { route ->
            KmbRouteStopEntity.fromApiModel(route)
        }

        kmbDao.clearRouteStopList()
        kmbDao.addRouteStopList(list)
    }
}
