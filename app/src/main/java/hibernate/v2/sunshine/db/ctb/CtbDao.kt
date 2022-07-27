package hibernate.v2.sunshine.db.ctb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface CtbDao {

    // ====

    @Query("SELECT * FROM ctb_stop")
    suspend fun getAllStopList(): List<CtbStopEntity>

    @Query("SELECT * FROM ctb_stop WHERE geohash IN (:list)")
    suspend fun getStopList(list: List<String>): List<CtbStopEntity>

    @Query("SELECT * FROM ctb_stop WHERE ctb_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<CtbStopEntity>

    @Query("SELECT * FROM ctb_stop LIMIT 1")
    suspend fun getSingleStop(): CtbStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<CtbStopEntity>)

    @Query("DELETE FROM ctb_stop")
    suspend fun clearStopList()

    // ====

    @Query("SELECT * FROM ctb_route WHERE ctb_route_company = (:company)")
    suspend fun getRouteList(company: String): List<CtbRouteEntity>

    @Query("SELECT * FROM ctb_route LIMIT 1")
    suspend fun getSingleRoute(): CtbRouteEntity?

    @Query("SELECT * FROM ctb_route WHERE ctb_route_id = (:routeId) AND ctb_route_bound = (:bound)")
    suspend fun getRoute(
        routeId: String,
        bound: String,
    ): CtbRouteEntity?

    @Insert
    suspend fun addRoute(entity: CtbRouteEntity)

    @Insert
    suspend fun addRouteList(entityList: List<CtbRouteEntity>)

    @Query("DELETE FROM ctb_route")
    suspend fun clearRouteList()

    @Transaction
    @Query("SELECT * FROM ctb_route_stop WHERE ctb_route_stop_company = (:company)")
    suspend fun getRouteStopComponentList(company: String): List<CtbRouteStopComponent>

    @Transaction
    @Query("SELECT * FROM ctb_route_stop WHERE ctb_route_stop_company = (:company) AND ctb_route_stop_route_id = (:route) AND ctb_route_stop_bound = (:bound)")
    suspend fun getRouteStopComponentList(
        company: String,
        route: String,
        bound: String
    ): List<CtbRouteStopComponent>

    @Query("SELECT * FROM ctb_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): CtbRouteStopEntity?

    @Query("SELECT * FROM ctb_route_stop WHERE ctb_route_stop_route_id = (:routeId) AND ctb_route_stop_bound = (:bound)")
    suspend fun getRouteStop(
        routeId: String,
        bound: String
    ): List<CtbRouteStopEntity>

    @Insert
    suspend fun addRouteStopList(entityList: List<CtbRouteStopEntity>)

    @Query("DELETE FROM ctb_route_stop")
    suspend fun clearRouteStopList()

    @Query("SELECT * FROM ctb_route_stop WHERE ctb_route_stop_stop_id = (:stopId)")
    suspend fun getRouteStopListFromStopId(stopId: String): List<CtbRouteStopEntity>

    @RawQuery
    suspend fun getRouteListFromRouteId(query: SupportSQLiteQuery): List<CtbRouteEntity>

    suspend fun getRouteListFromRouteId(routeStopList: List<CtbRouteStopEntity>): List<CtbRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM ctb_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)

            var where = "(ctb_route_id = ? AND ctb_route_bound = ?)"

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
}
