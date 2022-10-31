package hibernate.v2.database.ctb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.ctb.CtbRoute
import hibernate.v2.api.model.transport.ctb.CtbRouteStop
import hibernate.v2.api.model.transport.ctb.CtbStop
import hibernate.v2.database.DatabaseDriverFactory
import hibernate.v2.database.DatabaseFactory
import hibernatev2database.Ctb_route
import hibernatev2database.Ctb_route_stop
import hibernatev2database.Ctb_stop

class CtbDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = DatabaseFactory.createDatabase(driver)
    private val queries = database.ctbDaoQueries

    fun addStopList(list: List<CtbStop>) {
        queries.transaction {
            list.forEach { queries.addStopList(convertFrom(it)) }
        }
    }

    fun addRouteStopList(list: List<CtbRouteStop>) {
        queries.transaction {
            list.forEach { queries.addRouteStopList(convertFrom(it)) }
        }
    }

    fun addRouteList(list: List<CtbRoute>) {
        queries.transaction {
            list.forEach { queries.addRouteList(convertFrom(it)) }
        }
    }

    fun getStopList(list: List<String>): List<CtbStopEntity> {
        return queries
            .getStopList(list)
            .executeAsList().map {
                CtbStopEntity.convertFrom(it)
            }
    }

    fun getRouteList(company: Company): List<CtbRouteEntity> {
        return queries
            .getAllRouteListByCompany(company)
            .executeAsList().map {
                CtbRouteEntity.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(company: Company): List<CtbRouteStopComponent> {
        return queries
            .getRouteStopComponentAllList(company)
            .executeAsList().map {
                CtbRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopComponentList(
        company: Company,
        route: String,
        bound: Bound
    ): List<CtbRouteStopComponent> {
        return queries
            .getRouteStopComponentList(company,route, bound)
            .executeAsList().map {
                CtbRouteStopComponent.convertFrom(it)
            }
    }

    fun getRouteStopListFromStopId(stopId: String): List<CtbRouteStopEntity> {
        return queries
            .getRouteStopListFromStopId(stopId)
            .executeAsList().map {
                CtbRouteStopEntity.convertFrom(it)
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

    private fun convertFrom(item: CtbStop) = Ctb_stop(
        ctb_stop_id = item.stopId,
        name_en = item.nameEn,
        name_tc = item.nameTc,
        name_sc = item.nameSc,
        lat = item.lat.toDoubleOrNull() ?: 0.0,
        lng = item.lng.toDoubleOrNull() ?: 0.0,
        geohash = item.geohash,
    )

    private fun convertFrom(item: CtbRouteStop) = Ctb_route_stop(
        ctb_route_stop_route_id = item.routeId,
        ctb_route_stop_bound = item.bound,
        ctb_route_stop_company = item.company,
        ctb_route_stop_seq = item.seq.toLong(),
        ctb_route_stop_stop_id = item.stopId,
    )

    private fun convertFrom(item: CtbRoute) = Ctb_route(
        ctb_route_id = item.routeId,
        ctb_route_bound = item.bound,
        ctb_route_company = item.company,
        orig_en = item.origEn,
        orig_tc = item.origTc,
        orig_sc = item.origSc,
        dest_en = item.destEn,
        dest_tc = item.destTc,
        dest_sc = item.destSc,
    )

    fun getRouteListFromRouteId(routeStopList: List<CtbRouteStopEntity>): List<CtbRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM ctb_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where =
                "(ctb_route_id = (?) AND ctb_route_bound = (?) AND ctb_route_service_type = (?))"

            if (routeStopList.lastIndex != index) {
                where += " OR "
            }

            mainQuery += where
        }

        val cursor = driver.executeQuery(1, mainQuery, bindArgs.size) {
            bindArgs.forEachIndexed { index, arg ->
                bindString(index, arg)
            }
        }

        val result = arrayListOf<CtbRouteEntity>()
        while (cursor.next()) {
            val ctbRoute = Ctb_route(
                ctb_route_id = cursor.getString(0)!!,
                ctb_route_bound = Bound.from(cursor.getString(1)),
                ctb_route_company = Company.from(cursor.getString(2)),
                orig_en = cursor.getString(3)!!,
                orig_tc = cursor.getString(4)!!,
                orig_sc = cursor.getString(5)!!,
                dest_en = cursor.getString(6)!!,
                dest_tc = cursor.getString(7)!!,
                dest_sc = cursor.getString(8)!!
            )
            result.add(CtbRouteEntity.convertFrom(ctbRoute))
        }

        return result
    }
}