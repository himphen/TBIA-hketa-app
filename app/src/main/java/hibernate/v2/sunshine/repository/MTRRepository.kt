package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import hibernate.v2.api.model.transport.mtr.MTRRoute
import hibernate.v2.api.model.transport.mtr.MTRRouteStop
import hibernate.v2.api.model.transport.mtr.MTRStop
import hibernate.v2.sunshine.db.mtr.MTRDao
import hibernate.v2.sunshine.db.mtr.MTRRouteEntity
import hibernate.v2.sunshine.db.mtr.MTRRouteStopEntity
import hibernate.v2.sunshine.db.mtr.MTRStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.util.getSnapshotValue

class MTRRepository(
    private val mtrDao: MTRDao,
) : BaseRepository() {
    private val dbName = FIREBASE_DB_MTR

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<MTRRoute>>()?.let { list ->
            saveRouteList(list.map { route ->
                MTRRouteEntity.fromApiModel(route)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<MTRRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { routeStop ->
                MTRRouteStopEntity.fromApiModel(routeStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<MTRStop>>()?.let { list ->
            saveStopList(list.map { stop ->
                MTRStopEntity.fromApiModel(stop)
            })
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