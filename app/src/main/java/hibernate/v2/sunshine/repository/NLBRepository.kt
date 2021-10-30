package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.getValue
import hibernate.v2.api.model.transport.nlb.NLBRoute
import hibernate.v2.api.model.transport.nlb.NLBRouteStop
import hibernate.v2.api.model.transport.nlb.NLBStop
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
            saveRouteList(list.map { nlbRoute ->
                NLBRouteEntity.fromApiModel(nlbRoute)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NLBRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { nlbRouteStop ->
                NLBRouteStopEntity.fromApiModel(nlbRouteStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NLBStop>>()?.let { list ->
            saveStopList(list.map { nlbStop ->
                NLBStopEntity.fromApiModel(nlbStop)
            })
        }
    }

    suspend fun getRouteListByCompanyDb() = dao.getRouteList()
    suspend fun getRouteStopComponentListDb() =
        dao.getRouteStopComponentList()

    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = dao.getRouteStopComponentList(
        route.company.value,
        route.routeId,
        route.bound.value
    )

    suspend fun hasStopListDb() = dao.getSingleStop() != null
    suspend fun hasRouteListDb() = dao.getSingleRoute() != null
    suspend fun hasRouteStopListDb() = dao.getSingleRouteStop() != null

    suspend fun isDataExisted() = hasStopListDb() && hasRouteListDb() && hasRouteStopListDb()

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

    suspend fun getStopListDb() = dao.getStopList()

    suspend fun getRouteListFromStopId(stopId: String): List<TransportRoute> {
        val routeStopList = dao.getRouteStopListFromStopId(stopId)
        val routeList = dao.getRouteListFromRouteId(routeStopList)

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
        val routeStopList = dao.getRouteStopListFromStopId(stop.stopId)
        val routeList = dao.getRouteListFromRouteId(routeStopList)
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