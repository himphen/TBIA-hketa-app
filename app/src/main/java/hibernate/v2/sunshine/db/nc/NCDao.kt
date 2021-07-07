package hibernate.v2.sunshine.db.nc

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface NCDao {

    //////

    @Query("SELECT * FROM nc_stop")
    suspend fun getStopList(): List<NCStopEntity>

    @Query("SELECT * FROM nc_stop WHERE nc_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<NCStopEntity>

    @Query("SELECT * FROM nc_stop LIMIT 1")
    suspend fun getSingleStop(): NCStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<NCStopEntity>)

    @Query("DELETE FROM nc_stop")
    suspend fun clearStopList()

    //////

    @Query("SELECT * FROM nc_route")
    suspend fun getRouteList(): List<NCRouteEntity>

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

    //////

//    @Query("SELECT * FROM nc_route_stop INNER " +
//            "JOIN nc_stop ON nc_route_stop.stop = nc_stop.stop " +
//            "WHERE nc_route_stop.route = (:routeId) " +
//            "AND nc_route_stop.bound = (:bound) " +
//            "AND nc_route_stop.service_type = (:serviceType)")
//    suspend fun getRouteStopDetailsList(
//        routeId: String,
//        bound: Bound,
//        serviceType: String,
//    ): List<NCRouteStopDetailsEntity>

    @Query("SELECT * FROM nc_route_stop")
    suspend fun getRouteStopList(): List<NCRouteStopEntity>

    @Transaction
    @Query("SELECT * FROM nc_route_stop")
    suspend fun getRouteStopDetailsList(): List<NCRouteStopDetails>

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
}
