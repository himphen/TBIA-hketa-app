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

    @Query("SELECT * FROM nc_stop WHERE stop = (:stop)")
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

//    @Transaction
//    @Query("SELECT * FROM nc_route " +
//            "JOIN nc_route_stop ON " +
//            "nc_route.route = nc_route_stop.route " +
//            "AND nc_route.service_type = nc_route_stop.service_type " +
//            "AND nc_route.bound = nc_route_stop.bound " +
//            "JOIN nc_stop ON " +
//            "nc_route_stop.stop = nc_stop.stop")
//    suspend fun getRouteWithStopList(): List<NCRouteAndStops>

    @Query("SELECT * FROM nc_route LIMIT 1")
    suspend fun getSingleRoute(): NCRouteEntity?

    @Query("SELECT * FROM nc_route WHERE route = (:route) AND bound = (:bound)")
    suspend fun getRoute(
        route: String,
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

    @Query("SELECT * FROM nc_route_stop WHERE route = (:route) AND bound = (:bound)")
    suspend fun getRouteStop(
        route: String,
        bound: String
    ): List<NCRouteStopEntity>

    @Insert
    suspend fun addRouteStopList(entityList: List<NCRouteStopEntity>)

    @Query("DELETE FROM nc_route_stop")
    suspend fun clearRouteStopList()
}
