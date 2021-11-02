package hibernate.v2.sunshine.db.nlb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface NLBDao {

    //////

    @Query("SELECT * FROM nlb_stop")
    suspend fun getStopList(): List<NLBStopEntity>

    @Query("SELECT * FROM nlb_stop WHERE nlb_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<NLBStopEntity>

    @Query("SELECT * FROM nlb_stop LIMIT 1")
    suspend fun getSingleStop(): NLBStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<NLBStopEntity>)

    @Query("DELETE FROM nlb_stop")
    suspend fun clearStopList()

    //////

    @Query("SELECT * FROM nlb_route")
    suspend fun getRouteList(): List<NLBRouteEntity>

    @Query("SELECT * FROM nlb_route LIMIT 1")
    suspend fun getSingleRoute(): NLBRouteEntity?

    @Query("SELECT * FROM nlb_route WHERE nlb_route_id = (:route)")
    suspend fun getRoute(
        route: String
    ): NLBRouteEntity?

    @Insert
    suspend fun addRouteList(entityList: List<NLBRouteEntity>)

    @Query("DELETE FROM nlb_route")
    suspend fun clearRouteList()

    //////

    @Query("SELECT * FROM nlb_route_stop WHERE nlb_route_stop_stop_id = (:stopId)")
    suspend fun getRouteStopListFromStopId(stopId: String): List<NLBRouteStopEntity>

    @RawQuery
    suspend fun getRouteListFromRouteId(query: SupportSQLiteQuery): List<NLBRouteEntity>

    suspend fun getRouteListFromRouteId(routeStopList: List<NLBRouteStopEntity>): List<NLBRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM nlb_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where = "(nlb_route_id = ? AND nlb_route_bound = ? AND nlb_route_service_type = ?)"

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
    @Query("SELECT * FROM nlb_route_stop")
    suspend fun getRouteStopComponentList(): List<NLBRouteStopComponent>

    @Transaction
    @Query("SELECT * FROM nlb_route_stop WHERE nlb_route_stop_route_id = (:route)")
    suspend fun getRouteStopComponentList(
        route: String
    ): List<NLBRouteStopComponent>

    @Query("SELECT * FROM nlb_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): NLBRouteStopEntity?

    @Insert
    suspend fun addRouteStopList(entityList: List<NLBRouteStopEntity>)

    @Query("DELETE FROM nlb_route_stop")
    suspend fun clearRouteStopList()
}
