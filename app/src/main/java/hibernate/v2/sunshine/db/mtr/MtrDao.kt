package hibernate.v2.sunshine.db.mtr

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface MtrDao {

    // ====

    @Query("SELECT * FROM mtr_stop")
    suspend fun getStopList(): List<MtrStopEntity>

    @Query("SELECT * FROM mtr_stop WHERE mtr_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<MtrStopEntity>

    @Query("SELECT * FROM mtr_stop LIMIT 1")
    suspend fun getSingleStop(): MtrStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<MtrStopEntity>)

    @Query("DELETE FROM mtr_stop")
    suspend fun clearStopList()

    // ====

    @Query("SELECT * FROM mtr_route")
    suspend fun getRouteList(): List<MtrRouteEntity>

    @Query("SELECT * FROM mtr_route WHERE route_info_is_enabled = (:isEnabled)")
    suspend fun getRouteList(isEnabled: Boolean = true): List<MtrRouteEntity>

    @Query("SELECT * FROM mtr_route LIMIT 1")
    suspend fun getSingleRoute(): MtrRouteEntity?

    @Query("SELECT * FROM mtr_route WHERE mtr_route_id = (:route) AND mtr_route_bound = (:bound) AND mtr_route_service_type = (:serviceType)")
    suspend fun getRoute(
        route: String,
        bound: String,
        serviceType: String,
    ): MtrRouteEntity?

    @Insert
    suspend fun addRouteList(entityList: List<MtrRouteEntity>)

    @Query("DELETE FROM mtr_route")
    suspend fun clearRouteList()

    // ====

    @Query("SELECT * FROM mtr_route_stop WHERE mtr_route_stop_stop_id = (:stopId)")
    suspend fun getRouteStopListFromStopId(stopId: String): List<MtrRouteStopEntity>

    @RawQuery
    suspend fun getRouteListFromRouteId(query: SupportSQLiteQuery): List<MtrRouteEntity>

    suspend fun getRouteListFromRouteId(routeStopList: List<MtrRouteStopEntity>): List<MtrRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM mtr_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where = "(mtr_route_id = ? AND mtr_route_bound = ? AND mtr_route_service_type = ?)"

            if (routeStopList.lastIndex != index) {
                where += " OR "
            }

            mainQuery += where
        }

        return getRouteListFromRouteId(
            SimpleSQLiteQuery(
                mainQuery,
                bindArgs.toTypedArray()
            )
        )
    }

    @Transaction
    @Query("SELECT * FROM mtr_route_stop")
    suspend fun getRouteStopComponentList(): List<MtrRouteStopComponent>

    @Transaction
    @Query("SELECT * FROM mtr_route_stop WHERE mtr_route_stop_route_id = (:route) AND mtr_route_stop_bound = (:bound) AND mtr_route_stop_service_type = (:serviceType)")
    suspend fun getRouteStopComponentList(
        route: String,
        bound: String,
        serviceType: String,
    ): List<MtrRouteStopComponent>

    @Query("SELECT * FROM mtr_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): MtrRouteStopEntity?

    @Insert
    suspend fun addRouteStopList(entityList: List<MtrRouteStopEntity>)

    @Query("DELETE FROM mtr_route_stop")
    suspend fun clearRouteStopList()
}
