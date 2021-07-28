package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import hibernate.v2.api.model.transport.nc.NCRoute
import hibernate.v2.api.model.transport.nc.NCRouteStop
import hibernate.v2.api.model.transport.nc.NCStop
import hibernate.v2.sunshine.db.nc.NCDao
import hibernate.v2.sunshine.db.nc.NCRouteEntity
import hibernate.v2.sunshine.db.nc.NCRouteStopEntity
import hibernate.v2.sunshine.db.nc.NCStopEntity
import hibernate.v2.sunshine.model.searchmap.SearchMapRoute
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.util.getSnapshotValue

class NCRepository(
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

    suspend fun getRouteListDb() = ncDao.getRouteList()
    suspend fun getRouteStopComponentListDb() = ncDao.getRouteStopComponentList()

    suspend fun hasStopListDb() = ncDao.getSingleStop() != null
    suspend fun hasRouteListDb() = ncDao.getSingleRoute() != null
    suspend fun hasRouteStopListDb() = ncDao.getSingleRouteStop() != null

    suspend fun isDataExisted() = hasStopListDb() && hasRouteListDb() && hasRouteStopListDb()

    suspend fun initDatabase() {
        ncDao.clearRouteList()
        ncDao.clearStopList()
        ncDao.clearRouteStopList()
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

    suspend fun getStopListDb() = ncDao.getStopList()

    suspend fun getRouteListFromStopId(stopId: String): List<TransportRoute> {
        val routeStopList = ncDao.getRouteStopListFromStopId(stopId)
        val routeList = ncDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }

    suspend fun setMapRouteListIntoMapStop(stopList: List<SearchMapStop>): List<SearchMapStop> {
        return stopList.map {
            if (it.mapRouteList.isEmpty()) {
                it.mapRouteList = getMapRouteList(it.stopId)
            }
            it
        }
    }

    suspend fun getMapRouteList(stopId: String): List<SearchMapRoute> {
        val routeStopList = ncDao.getRouteStopListFromStopId(stopId)
        val routeList = ncDao.getRouteListFromRouteId(routeStopList)
        val routeHashMap = routeList.map {
            it.routeHashId() to it
        }.toMap()

        return routeStopList.mapNotNull {
            val route = routeHashMap[it.routeHashId()] ?: return@mapNotNull null

            SearchMapRoute(
                route.toTransportModel(),
                it.seq
            )
        }
    }
}