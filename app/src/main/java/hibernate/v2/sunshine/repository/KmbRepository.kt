package hibernate.v2.sunshine.repository

import com.fonfon.kgeohash.GeoHash
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
    private val kmbDao: KmbDao,
) : BaseRepository() {
    private val dbName = FIREBASE_DB_KMB

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<KmbRoute>>()?.let { list ->
            val temp = list
                .map(KmbRouteEntity.Companion::fromApiModel)
                .toMutableList()
                .apply { sortWith(KmbRouteEntity::compareTo) }

            saveRouteList(temp)
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

    suspend fun getStopListDb(list: List<GeoHash>) = kmbDao.getStopList(list.map { it.toString() })
    suspend fun getRouteListDb() = kmbDao.getRouteList()
    suspend fun getRouteStopComponentListDb() = kmbDao.getRouteStopComponentList()
    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = kmbDao.getRouteStopComponentList(
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
        val routeStopList = kmbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList =
            kmbDao.getRouteListFromRouteId(routeStopList).filter { !it.isSpecialRoute() }
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

    suspend fun getStopDb(stop: String): KmbStopEntity? = kmbDao.getStop(stop).firstOrNull()

    suspend fun getRouteDb(
        route: String,
        bound: String,
        serviceType: String,
    ): KmbRouteEntity? = kmbDao.getRoute(
        route,
        bound,
        serviceType,
    )

    suspend fun initDatabase() {
        kmbDao.clearRouteList()
        kmbDao.clearStopList()
        kmbDao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<KmbRouteEntity>) {
        kmbDao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<KmbRouteStopEntity>) {
        kmbDao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<KmbStopEntity>) {
        kmbDao.addStopList(entityList)
    }
}
