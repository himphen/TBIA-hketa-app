package hibernate.v2.database

import hibernate.v2.api.CommonLogger
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.model.transport.kmb.KmbRouteStop
import hibernate.v2.api.model.transport.kmb.KmbStop
import hibernate.v2.database.kmb.KmbRouteEntity
import hibernate.v2.database.kmb.KmbRouteStopEntity
import hibernatev2database.Kmb_route
import hibernatev2database.Kmb_route_stop
import hibernatev2database.Kmb_stop

class LocalDatabaseKmm(val databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()

    private val kmbDatabase = KmbDatabase(driver)

    fun saveStopList(list: List<KmbStop>) {
        kmbDatabase.kmbDatabaseQueries.transaction {
            list.forEach { kmbDatabase.kmbDatabaseQueries.addStopList(convertFrom(it)) }
        }
    }

    fun saveRouteStopList(list: List<KmbRouteStop>) {
        kmbDatabase.kmbDatabaseQueries.transaction {
            list.forEach { kmbDatabase.kmbDatabaseQueries.addRouteStopList(convertFrom(it)) }
        }
    }

    fun saveRouteList(list: List<KmbRoute>) {
        kmbDatabase.kmbDatabaseQueries.transaction {
            list.forEach { kmbDatabase.kmbDatabaseQueries.addRouteList(convertFrom(it)) }
        }
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
        kmb_route_bound = item.bound,
        kmb_route_service_type = item.serviceType,
        orig_en = item.origEn,
        orig_tc = item.origTc,
        orig_sc = item.origSc,
        dest_en = item.destEn,
        dest_tc = item.destTc,
        dest_sc = item.destSc,
    )

    fun meow(routeStopList: List<KmbRouteStopEntity>): List<KmbRouteEntity> {
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

        val cursor = driver.executeQuery(1, mainQuery, bindArgs.size) {
            bindArgs.forEachIndexed { index, arg ->
                bindString(index, arg)
            }
        }

        while (cursor.next()) {
            CommonLogger.d(cursor.getString(0))
            CommonLogger.d(cursor.getString(1))
            CommonLogger.d(cursor.getString(2))
            CommonLogger.d(cursor.getString(3))
            CommonLogger.d(cursor.getString(4))
            CommonLogger.d(cursor.getString(5))
        }

        return getRouteListFromRouteId(
            SimpleSQLiteQuery(
                mainQuery,
                bindArgs.toTypedArray()
            )
        )
    }
}