package hibernate.v2.sunshine.db.lrt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface LRTDao {

    // ====

    @Query("SELECT * FROM lrt_stop")
    suspend fun getStopList(): List<LRTStopEntity>

    @Query("SELECT * FROM lrt_stop WHERE lrt_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<LRTStopEntity>

    @Query("SELECT * FROM lrt_stop LIMIT 1")
    suspend fun getSingleStop(): LRTStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<LRTStopEntity>)

    @Query("DELETE FROM lrt_stop")
    suspend fun clearStopList()

    // ====

    // ====

    @Query("SELECT * FROM lrt_route")
    suspend fun getRouteList(): List<LRTRouteEntity>

    @Query("SELECT * FROM lrt_route WHERE route_info_is_enabled = (:isEnabled)")
    suspend fun getRouteList(isEnabled: Boolean = true): List<LRTRouteEntity>

    @Query("SELECT * FROM lrt_route LIMIT 1")
    suspend fun getSingleRoute(): LRTRouteEntity?

    @Query("SELECT * FROM lrt_route WHERE lrt_route_id = (:route) AND lrt_route_bound = (:bound) AND lrt_route_service_type = (:serviceType)")
    suspend fun getRoute(
        route: String,
        bound: String,
        serviceType: String,
    ): LRTRouteEntity?

    @Insert
    suspend fun addRouteList(entityList: List<LRTRouteEntity>)

    @Query("DELETE FROM lrt_route")
    suspend fun clearRouteList()

    // ====

    @Query("SELECT * FROM lrt_route_stop WHERE lrt_route_stop_stop_id = (:stopId)")
    suspend fun getRouteStopListFromStopId(stopId: String): List<LRTRouteStopEntity>

    @RawQuery
    suspend fun getRouteListFromRouteId(query: SupportSQLiteQuery): List<LRTRouteEntity>

    suspend fun getRouteListFromRouteId(routeStopList: List<LRTRouteStopEntity>): List<LRTRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM lrt_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where = "(lrt_route_id = ? AND lrt_route_bound = ? AND lrt_route_service_type = ?)"

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
    @Query("SELECT * FROM lrt_route_stop")
    suspend fun getRouteStopComponentList(): List<LRTRouteStopComponent>

    @Transaction
    @Query("SELECT * FROM lrt_route_stop WHERE lrt_route_stop_route_id = (:route) AND lrt_route_stop_bound = (:bound) AND lrt_route_stop_service_type = (:serviceType)")
    suspend fun getRouteStopComponentList(
        route: String,
        bound: String,
        serviceType: String,
    ): List<LRTRouteStopComponent>

    @Query("SELECT * FROM lrt_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): LRTRouteStopEntity?

    @Insert
    suspend fun addRouteStopList(entityList: List<LRTRouteStopEntity>)

    @Query("DELETE FROM lrt_route_stop")
    suspend fun clearRouteStopList()
}
