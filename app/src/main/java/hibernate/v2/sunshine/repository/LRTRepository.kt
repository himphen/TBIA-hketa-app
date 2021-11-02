package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.getValue
import hibernate.v2.api.model.transport.lrt.LRTRoute
import hibernate.v2.api.model.transport.lrt.LRTRouteStop
import hibernate.v2.api.model.transport.lrt.LRTStop
import hibernate.v2.sunshine.db.lrt.LRTDao
import hibernate.v2.sunshine.db.lrt.LRTRouteEntity
import hibernate.v2.sunshine.db.lrt.LRTRouteStopEntity
import hibernate.v2.sunshine.db.lrt.LRTStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.util.getSnapshotValue

class LRTRepository(
    private val lrtDao: LRTDao,
) : BaseRepository() {
    private val dbName = FIREBASE_DB_LRT

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<LRTRoute>>()?.let { list ->
            saveRouteList(list.map { route ->
                LRTRouteEntity.fromApiModel(route)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<LRTRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { routeStop ->
                LRTRouteStopEntity.fromApiModel(routeStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<LRTStop>>()?.let { list ->
            saveStopList(list.map { stop ->
                LRTStopEntity.fromApiModel(stop)
            })
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