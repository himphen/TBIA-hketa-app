package hibernate.v2.sunshine.repository

import com.fonfon.kgeohash.GeoHash
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.getValue
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.api.model.transport.gmb.GmbRoute
import hibernate.v2.api.model.transport.gmb.GmbRouteStop
import hibernate.v2.api.model.transport.gmb.GmbStop
import hibernate.v2.sunshine.db.gmb.GmbDao
import hibernate.v2.sunshine.db.gmb.GmbRouteEntity
import hibernate.v2.sunshine.db.gmb.GmbRouteStopEntity
import hibernate.v2.sunshine.db.gmb.GmbStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.util.getSnapshotValue

class GmbRepository(
    private val gmbDao: GmbDao,
) : BaseRepository() {
    private val dbName = FIREBASE_DB_GMB

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<GmbRoute>>()?.let { list ->
            saveRouteList(list.map { gmbRoute ->
                GmbRouteEntity.fromApiModel(gmbRoute)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<GmbRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { gmbRouteStop ->
                GmbRouteStopEntity.fromApiModel(gmbRouteStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<GmbStop>>()?.let { list ->
            saveStopList(list.map { gmbStop ->
                GmbStopEntity.fromApiModel(gmbStop)
            })
        }
    }

    suspend fun getRouteListDb(region: GmbRegion) = gmbDao.getRouteList(region.value)
    suspend fun getRouteStopComponentListDb(routeIdList: List<String>) =
        gmbDao.getRouteStopComponentList(routeIdList)
    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = gmbDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value
    )
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

    suspend fun getStopListDb(list: List<GeoHash>) = gmbDao.getStopList(list.map { it.toString() })

    suspend fun getRouteListFromStopId(stopId: String): List<TransportRoute> {
        val routeStopList = gmbDao.getRouteStopListFromStopId(stopId)
        val routeList = gmbDao.getRouteListFromRouteId(routeStopList)

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
        val routeStopList = gmbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = gmbDao.getRouteListFromRouteId(routeStopList)
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