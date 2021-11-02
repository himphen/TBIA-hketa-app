package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.getValue
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.model.transport.kmb.KmbRouteStop
import hibernate.v2.api.model.transport.kmb.KmbStop
import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.util.getSnapshotValue

class KmbRepository(
    private val dao: KmbDao,
) : BaseRepository() {
    private val dbName = FIREBASE_DB_KMB

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<KmbRoute>>()?.let { list ->
            saveRouteList(list.map { kmbRoute ->
                KmbRouteEntity.fromApiModel(kmbRoute)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<KmbRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { kmbRouteStop ->
                KmbRouteStopEntity.fromApiModel(kmbRouteStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<KmbStop>>()?.let { list ->
            saveStopList(list.map { kmbStop ->
                KmbStopEntity.fromApiModel(kmbStop)
            })
        }
    }

    suspend fun getStopListDb() = dao.getStopList()
    suspend fun getRouteListDb() = dao.getRouteList()
    suspend fun getRouteStopComponentListDb() = dao.getRouteStopComponentList()
    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = dao.getRouteStopComponentList(
        route.routeId,
        route.bound.value,
        route.serviceType
    )

    suspend fun setMapRouteListIntoMapStop(stopList: List<SearchMapStop>): List<SearchMapStop> {
        return stopList.map {
            if (it.mapRouteList.isEmpty()) {
                it.mapRouteList = getRouteEtaCardList(it)
            }
            it
        }
    }

    suspend fun getRouteEtaCardList(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = dao.getRouteStopListFromStopId(stop.stopId)
        val routeList =
            dao.getRouteListFromRouteId(routeStopList).filter { !it.isSpecialRoute() }
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

    suspend fun getStopDb(stop: String): KmbStopEntity? = dao.getStop(stop).firstOrNull()

    suspend fun getRouteDb(
        route: String,
        bound: String,
        serviceType: String,
    ): KmbRouteEntity? = dao.getRoute(
        route,
        bound,
        serviceType,
    )

    suspend fun initDatabase() {
        dao.clearRouteList()
        dao.clearStopList()
        dao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<KmbRouteEntity>) {
        dao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<KmbRouteStopEntity>) {
        dao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<KmbStopEntity>) {
        dao.addStopList(entityList)
    }
}
