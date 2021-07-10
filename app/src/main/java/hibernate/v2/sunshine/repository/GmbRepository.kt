package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import hibernate.v2.api.model.transport.gmb.GmbRoute
import hibernate.v2.api.model.transport.gmb.GmbRouteStop
import hibernate.v2.api.model.transport.gmb.GmbStop
import hibernate.v2.sunshine.db.gmb.GmbDao
import hibernate.v2.sunshine.db.gmb.GmbRouteEntity
import hibernate.v2.sunshine.db.gmb.GmbRouteStopEntity
import hibernate.v2.sunshine.db.gmb.GmbStopEntity
import hibernate.v2.sunshine.util.getSnapshotValue

class GmbRepository(
    private val gmbDao: GmbDao,
) {
    val database =
        Firebase.database("https://android-tv-c733a-default-rtdb.asia-southeast1.firebasedatabase.app/")

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child("gms_route")
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<GmbRoute>>()?.let { list ->
            saveRouteList(list.map { gmbRoute ->
                GmbRouteEntity.fromApiModel(gmbRoute)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child("gms_route_stop")
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<GmbRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { gmbRouteStop ->
                GmbRouteStopEntity.fromApiModel(gmbRouteStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child("gms_stop")
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<GmbStop>>()?.let { list ->
            saveStopList(list.map { gmbStop ->
                GmbStopEntity.fromApiModel(gmbStop)
            })
        }
    }

    suspend fun getRouteListDb() = gmbDao.getRouteList()
    suspend fun getRouteStopDetailsListDb() = gmbDao.getRouteStopDetailsList()

    suspend fun hasStopListDb() = gmbDao.getSingleStop() != null
    suspend fun hasRouteListDb() = gmbDao.getSingleRoute() != null
    suspend fun hasRouteStopListDb() = gmbDao.getSingleRouteStop() != null

    suspend fun isDataExisted() = hasStopListDb() && hasRouteListDb() && hasRouteStopListDb()

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
}