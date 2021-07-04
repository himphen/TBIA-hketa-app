package hibernate.v2.sunshine.repository

import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.api.model.nc.Company
import hibernate.v2.api.model.nc.NCRoute
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.nc.NCDao
import hibernate.v2.sunshine.db.nc.NCRouteEntity
import hibernate.v2.sunshine.db.nc.NCRouteStopEntity
import hibernate.v2.sunshine.db.nc.NCRouteWithRouteStop
import hibernate.v2.sunshine.db.nc.NCStopEntity

class NCRepository(
    private val apiManager: ApiManager,
    private val ncDao: NCDao,
) {

    suspend fun getStopListDb() = ncDao.getStopList()
    suspend fun getRouteListDb() = ncDao.getRouteList()
    suspend fun getRouteStopDetailsListDb() = ncDao.getRouteStopDetailsList()

    suspend fun hasStopListDb() = ncDao.getSingleStop() != null
    suspend fun hasRouteListDb() = ncDao.getSingleRoute() != null
    suspend fun hasRouteStopListDb() = ncDao.getSingleRouteStop() != null

    suspend fun isDataExisted() = hasStopListDb() && hasRouteListDb() && hasRouteStopListDb()

    suspend fun getRouteListApi(company: Company) =
        apiManager.ncService.getRouteList(company.value)

    suspend fun getNWFBRouteListApi() = getRouteListApi(Company.NWFB)

    suspend fun getCTBRouteListApi() = getRouteListApi(Company.CTB)

    suspend fun getStopApi(stop: String) =
        apiManager.ncService.getStop(stop)

    suspend fun getRouteStopListApi(
        company: Company,
        route: String,
        bound: Bound
    ) = apiManager.ncService.getRouteStop(company.value, route, bound.value)

    suspend fun getNWFBRouteStopListApi(
        route: String,
        bound: Bound
    ) = getRouteStopListApi(Company.NWFB, route, bound)

    suspend fun getCTBRouteStopListApi(
        route: String,
        bound: Bound
    ) = getRouteStopListApi(Company.CTB, route, bound)

    suspend fun initDatabase() {
        ncDao.clearRouteList()
        ncDao.clearStopList()
        ncDao.clearRouteStopList()
    }

    suspend fun getRouteWithRouteStop(route: NCRoute, bound: Bound): NCRouteWithRouteStop? {
        val routeStopList = getRouteStopListApi(route.company, route.routeId, bound).routeStopList
        if (routeStopList.isNotEmpty()) {
            val ncRouteEntity = NCRouteEntity.fromApiModel(route, bound)

            return NCRouteWithRouteStop(
                routeEntity = ncRouteEntity,
                entityList = routeStopList.map { routeStop ->
                    NCRouteStopEntity.fromApiModel(routeStop)
                }
            )
        }

        return null
    }

    suspend fun saveRouteList(entityList: List<NCRouteEntity>) {
        ncDao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<NCRouteStopEntity>) {
        ncDao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<NCStopEntity>) {
        ncDao.addStopList(entityList)
    }
}