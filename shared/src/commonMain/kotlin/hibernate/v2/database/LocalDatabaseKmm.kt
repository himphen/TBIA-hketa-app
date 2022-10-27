package hibernate.v2.database

import com.squareup.sqldelight.db.SqlPreparedStatement
import hibernate.v2.api.CommonLogger
import hibernate.v2.api.model.transport.kmb.KmbStop
import hibernate.v2.database.kmb.KmbRouteEntity
import hibernate.v2.database.kmb.KmbRouteStopEntityNew
import hibernatev2database.Kmb_stop

class LocalDatabaseKmm(val databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()

    private val kmbDatabase = KmbDatabase(driver)

    fun insert(list: List<KmbStop>) {
        kmbDatabase.kmbDatabaseQueries.transaction {
            list.forEach { kmbDatabase.kmbDatabaseQueries.addStopList(convertFrom(it)) }
        }
    }

    private fun convertFrom(stop: KmbStop): Kmb_stop {
        return Kmb_stop(
            kmb_stop_id = stop.stopId,
            name_en = stop.nameEn,
            name_tc = stop.nameTc,
            name_sc = stop.nameSc,
            lat = stop.lat.toDoubleOrNull() ?: 0.0,
            lng = stop.lng.toDoubleOrNull() ?: 0.0,
            geohash = stop.geohash,
        )
    }

    fun meow(routeStopList: List<KmbRouteStopEntityNew>): List<KmbRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM kmb_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where = "(kmb_route_id = (?) AND kmb_route_bound = (?) AND kmb_route_service_type = (?))"

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