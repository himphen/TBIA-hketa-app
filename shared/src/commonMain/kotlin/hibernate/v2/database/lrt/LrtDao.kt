package hibernate.v2.database.lrt

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.lrt.LrtRoute
import hibernate.v2.api.model.transport.lrt.LrtRouteStop
import hibernate.v2.api.model.transport.lrt.LrtStop
import hibernate.v2.database.DatabaseDriverFactory
import hibernate.v2.database.DatabaseFactory
import hibernatev2database.Lrt_route
import hibernatev2database.Lrt_route_stop
import hibernatev2database.Lrt_stop

class LrtDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = databaseDriverFactory.createDatabase()
    private val queries = database.lrtDaoQueries

    fun addStopList(list: List<LrtStop>) {
        queries.transaction {
            list.forEach { queries.addStopList(convertFrom(it)) }
        }
    }

    fun addRouteStopList(list: List<LrtRouteStop>) {
        queries.transaction {
            list.forEach { queries.addRouteStopList(convertFrom(it)) }
        }
    }

    fun addRouteList(list: List<LrtRoute>) {
        queries.transaction {
            list.forEach { queries.addRouteList(convertFrom(it)) }
        }
    }

    fun getStopList(): List<LrtStopEntity> {
        return queries
            .getAllStopList()
            .executeAsList().map {
                LrtStopEntity.convertFrom(it)
            }
    }

    fun getRouteList(): List<LrtRouteEntity> {
        return queries
            .getAllRouteList()
            .executeAsList().map {
                LrtRouteEntity.convertFrom(it)
            }
    }

    fun getEnabledRouteList(): List<LrtRouteEntity> {
        return queries
            .getAllEnabledRouteList()
            .executeAsList().map {
                LrtRouteEntity.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(): List<LrtRouteStopComponent> {
        return queries
            .getRouteStopComponentAllList()
            .executeAsList().map {
                LrtRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(
        routeId: String, bound: Bound, serviceType: String
    ): List<LrtRouteStopComponent> {
        return queries
            .getRouteStopComponentList(routeId, bound, serviceType)
            .executeAsList().map {
                LrtRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopListFromStopId(stopId: String): List<LrtRouteStopEntity> {
        return queries
            .getRouteStopListFromStopId(stopId)
            .executeAsList().map {
                LrtRouteStopEntity.convertFrom(it)
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

    private fun convertFrom(item: LrtStop) = Lrt_stop(
        lrt_stop_id = item.stopId,
        name_en = item.nameEn,
        name_tc = item.nameTc,
        name_sc = item.nameSc,
        lat = item.lat,
        lng = item.lng,
        geohash = item.geohash,
    )

    private fun convertFrom(item: LrtRouteStop) = Lrt_route_stop(
        lrt_route_stop_route_id = item.routeId,
        lrt_route_stop_bound = item.bound,
        lrt_route_stop_service_type = item.serviceType,
        lrt_route_stop_seq = item.seq.toLong(),
        lrt_route_stop_stop_id = item.stopId,
    )

    private fun convertFrom(item: LrtRoute) = Lrt_route(
        lrt_route_id = item.routeId,
        lrt_route_bound = item.bound,
        lrt_route_service_type = item.serviceType,
        orig_en = item.origEn,
        orig_tc = item.origTc,
        orig_sc = item.origSc,
        dest_en = item.destEn,
        dest_tc = item.destTc,
        dest_sc = item.destSc,
        route_info_name_color = item.routeInfo.color,
        route_info_is_enabled = item.routeInfo.isEnabled,
    )

    fun getRouteListFromRouteId(routeStopList: List<LrtRouteStopEntity>): List<LrtRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM lrt_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where =
                "(lrt_route_id = (?) AND lrt_route_bound = (?) AND lrt_route_service_type = (?))"

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

        val result = arrayListOf<LrtRouteEntity>()
        while (cursor.next()) {
            val lrtRoute = Lrt_route(
                lrt_route_id = cursor.getString(0)!!,
                lrt_route_bound = Bound.from(cursor.getString(1)),
                lrt_route_service_type = cursor.getString(2)!!,
                orig_en = cursor.getString(3)!!,
                orig_tc = cursor.getString(4)!!,
                orig_sc = cursor.getString(5)!!,
                dest_en = cursor.getString(6)!!,
                dest_tc = cursor.getString(7)!!,
                dest_sc = cursor.getString(8)!!,
                route_info_name_color = cursor.getString(9)!!,
                route_info_is_enabled = cursor.getLong(10)!! != 0L
            )
            result.add(LrtRouteEntity.convertFrom(lrtRoute))
        }

        return result
    }
}