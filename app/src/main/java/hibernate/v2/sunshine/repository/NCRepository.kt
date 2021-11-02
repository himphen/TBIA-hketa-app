package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.getValue
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.nc.NCRoute
import hibernate.v2.api.model.transport.nc.NCRouteStop
import hibernate.v2.api.model.transport.nc.NCStop
import hibernate.v2.sunshine.db.nc.NCDao
import hibernate.v2.sunshine.db.nc.NCRouteEntity
import hibernate.v2.sunshine.db.nc.NCRouteStopEntity
import hibernate.v2.sunshine.db.nc.NCStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.util.getSnapshotValue

class NCRepository(
    private val ncDao: NCDao,
) : BaseRepository() {
    private val dbName = FIREBASE_DB_NC

    @Throws(DatabaseException::class)
    suspend fun saveRouteListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NCRoute>>()?.let { list ->
            saveRouteList(list.map { ncRoute ->
                NCRouteEntity.fromApiModel(ncRoute)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveRouteStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_ROUTE_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NCRouteStop>>()?.let { list ->
            saveRouteStopList(list.map { ncRouteStop ->
                NCRouteStopEntity.fromApiModel(ncRouteStop)
            })
        }
    }

    @Throws(DatabaseException::class)
    suspend fun saveStopListFromFirebase() {
        val routeRef = database.reference.child(FIREBASE_REF_STOP + dbName)
        val snapshot = routeRef.getSnapshotValue()
        snapshot.getValue<List<NCStop>>()?.let { list ->
            saveStopList(list.map { ncStop ->
                NCStopEntity.fromApiModel(ncStop)
            })
        }
    }

    suspend fun getRouteListByCompanyDb(company: Company) = ncDao.getRouteList(company.value)
    suspend fun getRouteStopComponentListDb(company: Company) =
        ncDao.getRouteStopComponentList(company.value)

    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = ncDao.getRouteStopComponentList(
        route.company.value,
        route.routeId,
        route.bound.value
    )

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
                it.mapRouteList = getRouteEtaCardList(it)
            }
            it
        }
    }

    suspend fun getRouteEtaCardList(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = ncDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = ncDao.getRouteListFromRouteId(routeStopList)
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