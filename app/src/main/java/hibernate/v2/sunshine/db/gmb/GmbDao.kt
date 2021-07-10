package hibernate.v2.sunshine.db.gmb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface GmbDao {

    //////

    @Query("SELECT * FROM gmb_stop")
    suspend fun getStopList(): List<GmbStopEntity>

    @Query("SELECT * FROM gmb_stop WHERE gmb_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<GmbStopEntity>

    @Query("SELECT * FROM gmb_stop LIMIT 1")
    suspend fun getSingleStop(): GmbStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<GmbStopEntity>)

    @Query("DELETE FROM gmb_stop")
    suspend fun clearStopList()

    //////

    @Query("SELECT * FROM gmb_route")
    suspend fun getRouteList(): List<GmbRouteEntity>

    @Query("SELECT * FROM gmb_route LIMIT 1")
    suspend fun getSingleRoute(): GmbRouteEntity?

    @Query("SELECT * FROM gmb_route WHERE gmb_route_id = (:routeId) AND gmb_route_bound = (:bound)")
    suspend fun getRoute(
        routeId: String,
        bound: String,
    ): GmbRouteEntity?

    @Insert
    suspend fun addRoute(entity: GmbRouteEntity)

    @Insert
    suspend fun addRouteList(entityList: List<GmbRouteEntity>)

    @Query("DELETE FROM gmb_route")
    suspend fun clearRouteList()

    //////

//    @Query("SELECT * FROM gmb_route_stop INNER " +
//            "JOIN gmb_stop ON gmb_route_stop.stop = gmb_stop.stop " +
//            "WHERE gmb_route_stop.route = (:routeId) " +
//            "AND gmb_route_stop.bound = (:bound) " +
//            "AND gmb_route_stop.service_type = (:serviceType)")
//    suspend fun getRouteStopDetailsList(
//        routeId: String,
//        bound: Bound,
//        serviceType: String,
//    ): List<GmbRouteStopDetailsEntity>

    @Query("SELECT * FROM gmb_route_stop")
    suspend fun getRouteStopList(): List<GmbRouteStopEntity>

    @Transaction
    @Query("SELECT * FROM gmb_route_stop")
    suspend fun getRouteStopDetailsList(): List<GmbRouteStopDetails>

    @Query("SELECT * FROM gmb_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): GmbRouteStopEntity?

    @Query("SELECT * FROM gmb_route_stop WHERE gmb_route_stop_route_id = (:routeId) AND gmb_route_stop_bound = (:bound)")
    suspend fun getRouteStop(
        routeId: String,
        bound: String
    ): List<GmbRouteStopEntity>

    @Insert
    suspend fun addRouteStopList(entityList: List<GmbRouteStopEntity>)

    @Query("DELETE FROM gmb_route_stop")
    suspend fun clearRouteStopList()
}
