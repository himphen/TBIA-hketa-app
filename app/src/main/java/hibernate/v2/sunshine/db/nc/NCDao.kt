package hibernate.v2.sunshine.db.nc

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface NCDao {

    // ====

    @Query("SELECT * FROM nc_stop")
    suspend fun getAllStopList(): List<NCStopEntity>

    @Query("SELECT * FROM nc_stop WHERE geohash IN (:list)")
    suspend fun getStopList(list: List<String>): List<NCStopEntity>

    @Query("SELECT * FROM nc_stop WHERE nc_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<NCStopEntity>

    @Query("SELECT * FROM nc_stop LIMIT 1")
    suspend fun getSingleStop(): NCStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<NCStopEntity>)

    @Query("DELETE FROM nc_stop")
    suspend fun clearStopList()

    // ====

    @Query("SELECT * FROM nc_route WHERE nc_route_company = (:company)")
    suspend fun getRouteList(company: String): List<NCRouteEntity>

    @Query("SELECT * FROM nc_route LIMIT 1")
    suspend fun getSingleRoute(): NCRouteEntity?

    @Query("SELECT * FROM nc_route WHERE nc_route_id = (:routeId) AND nc_route_bound = (:bound)")
    suspend fun getRoute(
        routeId: String,
        bound: String,
    ): NCRouteEntity?

    @Insert
    suspend fun addRoute(entity: NCRouteEntity)

    @Insert
    suspend fun addRouteList(entityList: List<NCRouteEntity>)

    @Query("DELETE FROM nc_route")
    suspend fun clearRouteList()

    @Transaction
    @Query("SELECT * FROM nc_route_stop WHERE nc_route_stop_company = (:company)")
    suspend fun getRouteStopComponentList(company: String): List<NCRouteStopComponent>

    @Transaction
    @Query("SELECT * FROM nc_route_stop WHERE nc_route_stop_company = (:company) AND nc_route_stop_route_id = (:route) AND nc_route_stop_bound = (:bound)")
    suspend fun getRouteStopComponentList(
        company: String,
        route: String,
        bound: String
    ): List<NCRouteStopComponent>

    @Query("SELECT * FROM nc_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): NCRouteStopEntity?

    @Query("SELECT * FROM nc_route_stop WHERE nc_route_stop_route_id = (:routeId) AND nc_route_stop_bound = (:bound)")
    suspend fun getRouteStop(
        routeId: String,
        bound: String
    ): List<NCRouteStopEntity>

    @Insert
    suspend fun addRouteStopList(entityList: List<NCRouteStopEntity>)

    @Query("DELETE FROM nc_route_stop")
    suspend fun clearRouteStopList()

    @Query("SELECT * FROM nc_route_stop WHERE nc_route_stop_stop_id = (:stopId)")
    suspend fun getRouteStopListFromStopId(stopId: String): List<NCRouteStopEntity>

    @RawQuery
    suspend fun getRouteListFromRouteId(query: SupportSQLiteQuery): List<NCRouteEntity>

    suspend fun getRouteListFromRouteId(routeStopList: List<NCRouteStopEntity>): List<NCRouteEntity> {
        if (routeStopList.isEmpty()) return emptyList()

        var mainQuery = "SELECT * FROM nc_route WHERE "
        val bindArgs = arrayListOf<String>()

        routeStopList.forEachIndexed { index, it ->
            bindArgs.add(it.routeId)
            bindArgs.add(it.bound.value)

            var where = "(nc_route_id = ? AND nc_route_bound = ?)"

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
