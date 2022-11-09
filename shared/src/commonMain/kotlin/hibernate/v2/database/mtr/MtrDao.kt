package hibernate.v2.database.mtr

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.mtr.MtrRoute
import hibernate.v2.api.model.transport.mtr.MtrRouteStop
import hibernate.v2.api.model.transport.mtr.MtrStop
import hibernate.v2.database.DatabaseDriverFactory
import hibernatev2database.Mtr_route
import hibernatev2database.Mtr_route_stop
import hibernatev2database.Mtr_stop

class MtrDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = databaseDriverFactory.createDatabase()
    private val queries = database.mtrDaoQueries

    fun addStopList(list: List<MtrStop>) {
        queries.transaction {
            list.forEach { queries.addStopList(convertFrom(it)) }
        }
    }

    fun addRouteStopList(list: List<MtrRouteStop>) {
        queries.transaction {
            list.forEach { queries.addRouteStopList(convertFrom(it)) }
        }
    }

    fun addRouteList(list: List<MtrRoute>) {
        queries.transaction {
            list.forEach { queries.addRouteList(convertFrom(it)) }
        }
    }

    fun getStopList(): List<MtrStopEntity> {
        return queries
            .getAllStopList()
            .executeAsList().map {
                MtrStopEntity.convertFrom(it)
            }
    }

    fun getRouteList(): List<MtrRouteEntity> {
        return queries
            .getAllRouteList()
            .executeAsList().map {
                MtrRouteEntity.convertFrom(it)
            }
    }

    fun getEnabledRouteList(): List<MtrRouteEntity> {
        return queries
            .getAllEnabledRouteList()
            .executeAsList().map {
                MtrRouteEntity.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(): List<MtrRouteStopComponent> {
        return queries
            .getRouteStopComponentAllList()
            .executeAsList().map {
                MtrRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(
        routeId: String, bound: Bound, serviceType: String
    ): List<MtrRouteStopComponent> {
        return queries
            .getRouteStopComponentList(routeId, bound, serviceType)
            .executeAsList().map {
                MtrRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopListFromStopId(stopId: String): List<MtrRouteStopEntity> {
        return queries
            .getRouteStopListFromStopId(stopId)
            .executeAsList().map {
                MtrRouteStopEntity.convertFrom(it)
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

    private fun convertFrom(item: MtrStop) = Mtr_stop(
        mtr_stop_id = item.stopId,
        name_en = item.nameEn,
        name_tc = item.nameTc,
        name_sc = item.nameSc,
        lat = item.lat,
        lng = item.lng,
        geohash = item.geohash,
    )

    private fun convertFrom(item: MtrRouteStop) = Mtr_route_stop(
        mtr_route_stop_route_id = item.routeId,
        mtr_route_stop_bound = item.bound,
        mtr_route_stop_service_type = item.serviceType,
        mtr_route_stop_seq = item.seq.toLong(),
        mtr_route_stop_stop_id = item.stopId,
    )

    private fun convertFrom(item: MtrRoute) = Mtr_route(
        mtr_route_id = item.routeId,
        mtr_route_bound = item.bound,
        mtr_route_service_type = item.serviceType,
        orig_en = item.origEn,
        orig_tc = item.origTc,
        orig_sc = item.origSc,
        dest_en = item.destEn,
        dest_tc = item.destTc,
        dest_sc = item.destSc,
        route_info_name_en = item.routeInfo.nameEn,
        route_info_name_tc = item.routeInfo.nameTc,
        route_info_name_sc = "",
        route_info_name_color = item.routeInfo.color,
        route_info_is_enabled = item.routeInfo.isEnabled,
    )

    fun getRouteListFromRouteId(routeStopList: List<MtrRouteStopEntity>): List<MtrRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM mtr_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where =
                "(mtr_route_id = (?) AND mtr_route_bound = (?) AND mtr_route_service_type = (?))"

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

        val result = arrayListOf<MtrRouteEntity>()
        while (cursor.next()) {
            val mtrRoute = Mtr_route(
                mtr_route_id = cursor.getString(0)!!,
                mtr_route_bound = Bound.from(cursor.getString(1)),
                mtr_route_service_type = cursor.getString(2)!!,
                orig_en = cursor.getString(3)!!,
                orig_tc = cursor.getString(4)!!,
                orig_sc = cursor.getString(5)!!,
                dest_en = cursor.getString(6)!!,
                dest_tc = cursor.getString(7)!!,
                dest_sc = cursor.getString(8)!!,
                route_info_name_en = cursor.getString(9)!!,
                route_info_name_tc = cursor.getString(10)!!,
                route_info_name_sc = cursor.getString(11)!!,
                route_info_name_color = cursor.getString(12)!!,
                route_info_is_enabled = cursor.getLong(13)!! != 0L
            )
            result.add(MtrRouteEntity.convertFrom(mtrRoute))
        }

        return result
    }
}