package hibernate.v2.database.gmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.api.model.transport.gmb.GmbRoute
import hibernate.v2.api.model.transport.gmb.GmbRouteStop
import hibernate.v2.api.model.transport.gmb.GmbStop
import hibernate.v2.database.DatabaseDriverFactory
import hibernatev2database.Gmb_route
import hibernatev2database.Gmb_route_stop
import hibernatev2database.Gmb_stop

class GmbDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = databaseDriverFactory.createDatabase()
    private val queries = database.gmbDaoQueries

    fun addStopList(list: List<GmbStop>) {
        queries.transaction {
            list.forEach { queries.addStopList(convertFrom(it)) }
        }
    }

    fun addRouteStopList(list: List<GmbRouteStop>) {
        queries.transaction {
            list.forEach { queries.addRouteStopList(convertFrom(it)) }
        }
    }

    fun addRouteList(list: List<GmbRoute>) {
        queries.transaction {
            list.forEach { queries.addRouteList(convertFrom(it)) }
        }
    }

    fun getStopList(list: List<String>): List<GmbStopEntity> {
        return queries
            .getStopList(list)
            .executeAsList().map {
                GmbStopEntity.convertFrom(it)
            }
    }

    fun getRouteList(region: GmbRegion): List<GmbRouteEntity> {
        return queries
            .getRouteListByRegion(region)
            .executeAsList().map {
                GmbRouteEntity.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(routeIdList: List<String>): List<GmbRouteStopComponent> {
        return queries
            .getRouteStopComponentAllList(routeIdList)
            .executeAsList().map {
                GmbRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(
        route: String,
        bound: Bound,
        serviceType:String
    ): List<GmbRouteStopComponent> {
        return queries
            .getRouteStopComponentList(route, bound, serviceType)
            .executeAsList().map {
                GmbRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopListFromStopId(stopId: String): List<GmbRouteStopEntity> {
        return queries
            .getRouteStopListFromStopId(stopId)
            .executeAsList().map {
                GmbRouteStopEntity.convertFrom(it)
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

    private fun convertFrom(item: GmbStop) = Gmb_stop(
        gmb_stop_id = item.stopId.toString(),
        name_en = item.nameEn,
        name_tc = item.nameTc,
        name_sc = item.nameSc,
        lat = item.lat,
        lng = item.lng,
        geohash = item.geohash,
    )

    private fun convertFrom(item: GmbRouteStop) = Gmb_route_stop(
        gmb_route_stop_route_id = item.routeId.toString(),
        gmb_route_stop_bound = item.bound,
        gmb_route_stop_service_type = item.serviceType.toString(),
        gmb_route_stop_seq = item.seq.toLong(),
        gmb_route_stop_stop_id = item.stopId.toString(),
    )

    private fun convertFrom(item: GmbRoute) = Gmb_route(
        gmb_route_id = item.routeId.toString(),
        gmb_route_no = item.routeNo,
        gmb_route_bound = item.bound,
        gmb_route_service_type = item.serviceType.toString(),
        orig_en = item.origEn,
        orig_tc = item.origTc,
        orig_sc = item.origSc,
        dest_en = item.destEn,
        dest_tc = item.destTc,
        dest_sc = item.destSc,
        region = item.region,
    )

    fun getRouteListFromRouteId(routeStopList: List<GmbRouteStopEntity>): List<GmbRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM gmb_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where =
                "(gmb_route_id = (?) AND gmb_route_bound = (?) AND gmb_route_service_type = (?))"

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

        val result = arrayListOf<GmbRouteEntity>()
        while (cursor.next()) {
            val gmbRoute = Gmb_route(
                gmb_route_id = cursor.getString(0)!!,
                gmb_route_no = cursor.getString(1)!!,
                gmb_route_bound = Bound.from(cursor.getString(2)),
                gmb_route_service_type = cursor.getString(3)!!,
                orig_en = cursor.getString(4)!!,
                orig_tc = cursor.getString(5)!!,
                orig_sc = cursor.getString(6)!!,
                dest_en = cursor.getString(7)!!,
                dest_tc = cursor.getString(8)!!,
                dest_sc = cursor.getString(9)!!,
                region = GmbRegion.from(cursor.getString(10)),
            )
            result.add(GmbRouteEntity.convertFrom(gmbRoute))
        }

        return result
    }
}