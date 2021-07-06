package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.api.model.nc.Company
import hibernate.v2.api.model.nc.NCRoute
import hibernate.v2.api.model.nc.NCRouteStop
import hibernate.v2.api.model.nc.NCStop
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.nc.NCDao
import hibernate.v2.sunshine.db.nc.NCRouteEntity
import hibernate.v2.sunshine.db.nc.NCRouteStopEntity
import hibernate.v2.sunshine.db.nc.NCRouteWithRouteStop
import hibernate.v2.sunshine.db.nc.NCStopEntity
import hibernate.v2.sunshine.util.getSnapshotValue

class NCRepository(
    private val apiManager: ApiManager,
    private val ncDao: NCDao,
) {
    val database =
        Firebase.database("https://android-tv-c733a-default-rtdb.asia-southeast1.firebasedatabase.app/")

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child("nwfb_citybus_route")
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NCRoute>>()?.let { list ->
            saveRouteList(list.map { ncRoute ->
                NCRouteEntity.fromApiModel(ncRoute)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child("nwfb_citybus_route_stop")
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NCRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { ncRouteStop ->
                NCRouteStopEntity.fromApiModel(ncRouteStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child("nwfb_citybus_stop")
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NCStop>>()?.let { list ->
            saveStopList(list.map { ncStop ->
                NCStopEntity.fromApiModel(ncStop)
            })
        }
    }

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
            val ncRouteEntity = NCRouteEntity.fromApiModel(route)

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