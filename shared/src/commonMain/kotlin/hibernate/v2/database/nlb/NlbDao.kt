package hibernate.v2.database.nlb

import hibernate.v2.api.model.transport.nlb.NlbRoute
import hibernate.v2.api.model.transport.nlb.NlbRouteStop
import hibernate.v2.api.model.transport.nlb.NlbStop
import hibernate.v2.database.DatabaseDriverFactory
import hibernate.v2.database.DatabaseFactory
import hibernatev2database.Nlb_route
import hibernatev2database.Nlb_route_stop
import hibernatev2database.Nlb_stop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NlbDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = DatabaseFactory.createDatabase(driver)
    private val queries = database.nlbDaoQueries

    fun addStopList(list: List<NlbStop>) {
        queries.transaction {
            list.forEach { queries.addStopList(convertFrom(it)) }
        }
    }

    fun addRouteStopList(list: List<NlbRouteStop>) {
        queries.transaction {
            list.forEach { queries.addRouteStopList(convertFrom(it)) }
        }
    }

    fun addRouteList(list: List<NlbRoute>) {
        queries.transaction {
            list.forEach { queries.addRouteList(convertFrom(it)) }
        }
    }

    suspend fun getStopList(list: List<String>): List<NlbStopEntity> {
        return withContext(Dispatchers.Main) {
            return@withContext queries
                .getStopList(list)
                .executeAsList().map {
                    NlbStopEntity.convertFrom(it)
                }
        }
    }

    suspend fun getRouteList(): List<NlbRouteEntity> {
        return queries
            .getAllRouteList()
            .executeAsList().map {
                NlbRouteEntity.convertFrom(it)
            }
    }

    suspend fun getRouteStopComponentList(): List<NlbRouteStopComponent> {
        return queries
            .getRouteStopComponentAllList()
            .executeAsList().map {
                NlbRouteStopComponent.convertFrom(it)
            }
    }

    suspend fun getRouteStopComponentList(
        routeId: String
    ): List<NlbRouteStopComponent> {
        return queries
            .getRouteStopComponentList(routeId)
            .executeAsList().map {
                NlbRouteStopComponent.convertFrom(it)
            }
    }

    suspend fun getRouteStopListFromStopId(stopId: String): List<NlbRouteStopEntity> {
        return queries
            .getRouteStopListFromStopId(stopId)
            .executeAsList().map {
                NlbRouteStopEntity.convertFrom(it)
            }
    }

    suspend fun clearRouteList() {
        queries.clearRouteList()
    }

    suspend fun clearStopList() {
        queries.clearStopList()
    }

    suspend fun clearRouteStopList() {
        queries.clearRouteStopList()
    }

    private fun convertFrom(item: NlbStop) = Nlb_stop(
        nlb_stop_id = item.stopId,
        name_en = item.nameEn,
        name_tc = item.nameTc,
        name_sc = item.nameSc,
        lat = item.lat.toDoubleOrNull() ?: 0.0,
        lng = item.lng.toDoubleOrNull() ?: 0.0,
        geohash = item.geohash,
    )

    private fun convertFrom(item: NlbRouteStop) = Nlb_route_stop(
        nlb_route_stop_route_id = item.routeId,
        nlb_route_stop_seq = item.seq.toLong(),
        nlb_route_stop_stop_id = item.stopId,
    )

    private fun convertFrom(item: NlbRoute) = Nlb_route(
        nlb_route_id = item.routeId,
        orig_en = item.origEn,
        orig_tc = item.origTc,
        orig_sc = item.origSc,
        dest_en = item.destEn,
        dest_tc = item.destTc,
        dest_sc = item.destSc,
    )

    suspend fun getRouteListFromRouteId(routeStopList: List<NlbRouteStopEntity>): List<NlbRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM nlb_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)

            var where = "(nlb_route_id = ?)"

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

        val result = arrayListOf<NlbRouteEntity>()
        while (cursor.next()) {
            val nlbRoute = Nlb_route(
                nlb_route_id = cursor.getString(0)!!,
                orig_en = cursor.getString(1)!!,
                orig_tc = cursor.getString(2)!!,
                orig_sc = cursor.getString(3)!!,
                dest_en = cursor.getString(4)!!,
                dest_tc = cursor.getString(5)!!,
                dest_sc = cursor.getString(6)!!
            )
            result.add(NlbRouteEntity.convertFrom(nlbRoute))
        }

        return result
    }

}
