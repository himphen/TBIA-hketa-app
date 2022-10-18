package hibernate.v2.sunshine.db.nlb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface NlbDao {

    // ====

    @Query("SELECT * FROM nlb_stop")
    suspend fun getAllStopList(): List<NlbStopEntity>

    @Query("SELECT * FROM nlb_stop WHERE geohash IN (:list)")
    suspend fun getStopList(list: List<String>): List<NlbStopEntity>

    @Query("SELECT * FROM nlb_stop WHERE nlb_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<NlbStopEntity>

    @Query("SELECT * FROM nlb_stop LIMIT 1")
    suspend fun getSingleStop(): NlbStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<NlbStopEntity>)

    @Query("DELETE FROM nlb_stop")
    suspend fun clearStopList()

    // ====

    @Query("SELECT * FROM nlb_route")
    suspend fun getRouteList(): List<NlbRouteEntity>

    @Query("SELECT * FROM nlb_route LIMIT 1")
    suspend fun getSingleRoute(): NlbRouteEntity?

    @Query("SELECT * FROM nlb_route WHERE nlb_route_id = (:route)")
    suspend fun getRoute(
        route: String
    ): NlbRouteEntity?

    @Insert
    suspend fun addRouteList(entityList: List<NlbRouteEntity>)

    @Query("DELETE FROM nlb_route")
    suspend fun clearRouteList()

    // ====

    @Query("SELECT * FROM nlb_route_stop WHERE nlb_route_stop_stop_id = (:stopId)")
    suspend fun getRouteStopListFromStopId(stopId: String): List<NlbRouteStopEntity>

    @RawQuery
    suspend fun getRouteListFromRouteId(query: SupportSQLiteQuery): List<NlbRouteEntity>

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

        return getRouteListFromRouteId(
            SimpleSQLiteQuery(
                mainQuery,
                bindArgs.toTypedArray()
            )
        )
    }

    @Transaction
    @Query("SELECT * FROM nlb_route_stop")
    suspend fun getRouteStopComponentList(): List<NlbRouteStopComponent>

    @Transaction
    @Query("SELECT * FROM nlb_route_stop WHERE nlb_route_stop_route_id = (:route)")
    suspend fun getRouteStopComponentList(
        route: String
    ): List<NlbRouteStopComponent>

    @Query("SELECT * FROM nlb_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): NlbRouteStopEntity?

    @Insert
    suspend fun addRouteStopList(entityList: List<NlbRouteStopEntity>)

    @Query("DELETE FROM nlb_route_stop")
    suspend fun clearRouteStopList()
}
