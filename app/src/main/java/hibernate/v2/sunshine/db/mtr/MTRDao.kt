package hibernate.v2.sunshine.db.mtr

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface MTRDao {

    //////

    @Query("SELECT * FROM mtr_stop")
    suspend fun getStopList(): List<MTRStopEntity>

    @Query("SELECT * FROM mtr_stop WHERE mtr_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<MTRStopEntity>

    @Query("SELECT * FROM mtr_stop LIMIT 1")
    suspend fun getSingleStop(): MTRStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<MTRStopEntity>)

    @Query("DELETE FROM mtr_stop")
    suspend fun clearStopList()

    //////

    @Query("SELECT * FROM mtr_route")
    suspend fun getRouteList(): List<MTRRouteEntity>

    @Query("SELECT * FROM mtr_route LIMIT 1")
    suspend fun getSingleRoute(): MTRRouteEntity?

    @Query("SELECT * FROM mtr_route WHERE mtr_route_id = (:route) AND mtr_route_bound = (:bound) AND mtr_route_service_type = (:serviceType)")
    suspend fun getRoute(
        route: String,
        bound: String,
        serviceType: String,
    ): MTRRouteEntity?

    @Insert
    suspend fun addRouteList(entityList: List<MTRRouteEntity>)

    @Query("DELETE FROM mtr_route")
    suspend fun clearRouteList()

    //////

    @Query("SELECT * FROM mtr_route_stop WHERE mtr_route_stop_stop_id = (:stopId)")
    suspend fun getRouteStopListFromStopId(stopId: String): List<MTRRouteStopEntity>

    @RawQuery
    suspend fun getRouteListFromRouteId(query: SupportSQLiteQuery): List<MTRRouteEntity>

    suspend fun getRouteListFromRouteId(routeStopList: List<MTRRouteStopEntity>): List<MTRRouteEntity> {
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
    suspend fun getRouteStopComponentList(): List<MTRRouteStopComponent>

    @Transaction
    @Query("SELECT * FROM mtr_route_stop WHERE mtr_route_stop_route_id = (:route) AND mtr_route_stop_bound = (:bound) AND mtr_route_stop_service_type = (:serviceType)")
    suspend fun getRouteStopComponentList(
        route: String,
        bound: String,
        serviceType: String,
    ): List<MTRRouteStopComponent>

    @Query("SELECT * FROM mtr_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): MTRRouteStopEntity?

    @Insert
    suspend fun addRouteStopList(entityList: List<MTRRouteStopEntity>)

    @Query("DELETE FROM mtr_route_stop")
    suspend fun clearRouteStopList()
}
