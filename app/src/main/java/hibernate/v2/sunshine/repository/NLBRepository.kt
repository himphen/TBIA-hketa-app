package hibernate.v2.sunshine.repository

import com.fonfon.kgeohash.GeoHash
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.getValue
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.model.transport.kmb.KmbRouteStop
import hibernate.v2.api.model.transport.kmb.KmbStop
import hibernate.v2.api.model.transport.nlb.NLBRoute
import hibernate.v2.api.model.transport.nlb.NLBRouteStop
import hibernate.v2.api.model.transport.nlb.NLBStop
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.db.nlb.NLBDao
import hibernate.v2.sunshine.db.nlb.NLBRouteEntity
import hibernate.v2.sunshine.db.nlb.NLBRouteStopEntity
import hibernate.v2.sunshine.db.nlb.NLBStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.util.getSnapshotValue

class NLBRepository(
    private val dao: NLBDao,
) : BaseRepository() {
    private val dbName = FIREBASE_DB_NLB

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NLBRoute>>()?.let { list ->
            saveRouteList(list.map { NLBRoute ->
                NLBRouteEntity.fromApiModel(NLBRoute)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NLBRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { NLBRouteStop ->
                NLBRouteStopEntity.fromApiModel(NLBRouteStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NLBStop>>()?.let { list ->
            saveStopList(list.map { NLBStop ->
                NLBStopEntity.fromApiModel(NLBStop)
            })
        }
    }

    suspend fun getStopListDb(list: List<GeoHash>) = dao.getStopList(list.map { it.toString() })
    suspend fun getRouteListDb() = dao.getRouteList()
    suspend fun getRouteStopComponentListDb() = dao.getRouteStopComponentList()
    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = dao.getRouteStopComponentList(
        route.routeId
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

    suspend fun getStopDb(stop: String): NLBStopEntity? = dao.getStop(stop).firstOrNull()

    suspend fun getRouteDb(
        route: String
    ): NLBRouteEntity? = dao.getRoute(
        route
    )

    suspend fun initDatabase() {
        dao.clearRouteList()
        dao.clearStopList()
        dao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<NLBRouteEntity>) {
        dao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<NLBRouteStopEntity>) {
        dao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<NLBStopEntity>) {
        dao.addStopList(entityList)
    }
}