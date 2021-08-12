package hibernate.v2.sunshine.db.kmb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface KmbDao {

    //////

    @Query("SELECT * FROM kmb_stop")
    suspend fun getStopList(): List<KmbStopEntity>

    @Query("SELECT * FROM kmb_stop WHERE kmb_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<KmbStopEntity>

    @Query("SELECT * FROM kmb_stop LIMIT 1")
    suspend fun getSingleStop(): KmbStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<KmbStopEntity>)

    @Query("DELETE FROM kmb_stop")
    suspend fun clearStopList()

    //////

    @Query("SELECT * FROM kmb_route")
    suspend fun getRouteList(): List<KmbRouteEntity>

    @Query("SELECT * FROM kmb_route LIMIT 1")
    suspend fun getSingleRoute(): KmbRouteEntity?

    @Query("SELECT * FROM kmb_route WHERE kmb_route_id = (:route) AND kmb_route_bound = (:bound) AND kmb_route_service_type = (:serviceType)")
    suspend fun getRoute(
        route: String,
        bound: String,
        serviceType: String,
    ): KmbRouteEntity?

    @Insert
    suspend fun addRouteList(entityList: List<KmbRouteEntity>)

    @Query("DELETE FROM kmb_route")
    suspend fun clearRouteList()

    //////

    @Query("SELECT * FROM kmb_route_stop WHERE kmb_route_stop_stop_id = (:stopId)")
    suspend fun getRouteStopListFromStopId(stopId: String): List<KmbRouteStopEntity>

    @RawQuery
    suspend fun getRouteListFromRouteId(query: SupportSQLiteQuery): List<KmbRouteEntity>

    suspend fun getRouteListFromRouteId(routeStopList: List<KmbRouteStopEntity>): List<KmbRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM kmb_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)
            bindArgs.add(it.serviceType)

            var where = "(kmb_route_id = ? AND kmb_route_bound = ? AND kmb_route_service_type = ?)"

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
    @Query("SELECT * FROM kmb_route_stop")
    suspend fun getRouteStopComponentList(): List<KmbRouteStopComponent>

    @Transaction
    @Query("SELECT * FROM kmb_route_stop WHERE kmb_route_stop_route_id = (:route) AND kmb_route_stop_bound = (:bound) AND kmb_route_stop_service_type = (:serviceType)")
    suspend fun getRouteStopComponentList(
        route: String,
        bound: String,
        serviceType: String,
    ): List<KmbRouteStopComponent>

    @Query("SELECT * FROM kmb_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): KmbRouteStopEntity?

    @Insert
    suspend fun addRouteStopList(entityList: List<KmbRouteStopEntity>)

    @Query("DELETE FROM kmb_route_stop")
    suspend fun clearRouteStopList()
}
