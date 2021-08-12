package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
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
    private val dao: NCDao,
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

    suspend fun getRouteListByCompanyDb(company: Company) = dao.getRouteList(company.value)
    suspend fun getRouteStopComponentListDb(company: Company) =
        dao.getRouteStopComponentList(company.value)

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

    suspend fun saveRouteList(entityList: List<NCRouteEntity>) {
        dao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<NCRouteStopEntity>) {
        dao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<NCStopEntity>) {
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