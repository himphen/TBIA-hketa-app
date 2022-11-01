package hibernate.v2.database.kmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.model.transport.kmb.KmbRouteStop
import hibernate.v2.api.model.transport.kmb.KmbStop
import hibernate.v2.database.DatabaseDriverFactory
import hibernatev2database.Kmb_route
import hibernatev2database.Kmb_route_stop
import hibernatev2database.Kmb_stop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KmbDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = databaseDriverFactory.createDatabase()
    private val queries = database.kmbDaoQueries

    fun addStopList(list: List<KmbStop>) {
        queries.transaction {
            list.forEach { queries.addStopList(convertFrom(it)) }
        }
    }

    fun addRouteStopList(list: List<KmbRouteStop>) {
        queries.transaction {
            list.forEach { queries.addRouteStopList(convertFrom(it)) }
        }
    }

    fun addRouteList(list: List<KmbRoute>) {
        queries.transaction {
            list.forEach { queries.addRouteList(convertFrom(it)) }
        }
    }

    suspend fun getStopList(list: List<String>): List<KmbStopEntity> {
        return withContext(Dispatchers.Main) {
            return@withContext queries
                .getStopList(list)
                .executeAsList().map {
                    KmbStopEntity.convertFrom(it)
                }
        }
    }

    fun getRouteList(): List<KmbRouteEntity> {
        return queries
            .getAllRouteList()
            .executeAsList().map {
                KmbRouteEntity.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(): List<KmbRouteStopComponent> {
        return queries
            .getRouteStopComponentAllList()
            .executeAsList().map {
                KmbRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(
        route: String,
        bound: Bound,
        serviceType: String
    ): List<KmbRouteStopComponent> {
        return queries
            .getRouteStopComponentList(route, bound, serviceType)
            .executeAsList().map {
                KmbRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopListFromStopId(stopId: String): List<KmbRouteStopEntity> {
        return queries
            .getRouteStopListFromStopId(stopId)
            .executeAsList().map {
                KmbRouteStopEntity.convertFrom(it)
            }
    }

    fun clearRouteList() {
        queries.clearRouteList()
    }

    fun clearStopList() {
        queries.clearStopList()
    }

    fun clearRouteStopList() {
        queries.clearRouteStopList()
    }

    private fun convertFrom(item: KmbStop) = Kmb_stop(
        kmb_stop_id = item.stopId,
        name_en = item.nameEn,
        name_tc = item.nameTc,
        name_sc = item.nameSc,
        lat = item.lat.toDoubleOrNull() ?: 0.0,
        lng = item.lng.toDoubleOrNull() ?: 0.0,
        geohash = item.geohash,
    )

    private fun convertFrom(item: KmbRouteStop) = Kmb_route_stop(
        kmb_route_stop_route_id = item.routeId,
        kmb_route_stop_bound = item.bound,
        kmb_route_stop_service_type = item.serviceType,
        kmb_route_stop_seq = item.seq.toLongOrNull() ?: 0,
        kmb_route_stop_stop_id = item.stopId,
    )

    private fun convertFrom(item: KmbRoute) = Kmb_route(
        kmb_route_id = item.routeId,
        kmb_route_bound = Bound.from(item.bound),
        kmb_route_service_type = item.serviceType,
        orig_en = item.origEn,
        orig_tc = item.origTc,
        orig_sc = item.origSc,
        dest_en = item.destEn,
        dest_tc = item.destTc,
        dest_sc = item.destSc,
    )

    fun getRouteListFromRouteId(routeStopList: List<KmbRouteStopEntity>): List<KmbRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM kmb_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where =
                "(kmb_route_id = (?) AND kmb_route_bound = (?) AND kmb_route_service_type = (?))"

            if (routeStopList.lastIndex != index) {
                where += " OR "
            }

            mainQuery += where
        }

        val cursor = driver.executeQuery(null, mainQuery, bindArgs.size) {
            bindArgs.forEachIndexed { index, arg ->
                bindString(index + 1, arg)
            }
        }

        val result = arrayListOf<KmbRouteEntity>()
        while (cursor.next()) {
            val kmbRoute = Kmb_route(
                kmb_route_id = cursor.getString(0)!!,
                kmb_route_bound = Bound.from(cursor.getString(1)),
                kmb_route_service_type = cursor.getString(2)!!,
                orig_en = cursor.getString(3)!!,
                orig_tc = cursor.getString(4)!!,
                orig_sc = cursor.getString(5)!!,
                dest_en = cursor.getString(6)!!,
                dest_tc = cursor.getString(7)!!,
                dest_sc = cursor.getString(8)!!
            )
            result.add(KmbRouteEntity.convertFrom(kmbRoute))
        }

        return result
    }
}