package hibernate.v2.sunshine.db.kmb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface KmbDao {

    //////

    @Query("SELECT * FROM kmb_stop")
    suspend fun getStopList(): List<KmbStopEntity>

    @Query("SELECT * FROM kmb_stop WHERE stop = (:stop)")
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

//    @Transaction
//    @Query("SELECT * FROM kmb_route " +
//            "JOIN kmb_route_stop ON " +
//            "kmb_route.route = kmb_route_stop.route " +
//            "AND kmb_route.service_type = kmb_route_stop.service_type " +
//            "AND kmb_route.bound = kmb_route_stop.bound " +
//            "JOIN kmb_stop ON " +
//            "kmb_route_stop.stop = kmb_stop.stop")
//    suspend fun getRouteWithStopList(): List<KmbRouteAndStops>

    @Query("SELECT * FROM kmb_route LIMIT 1")
    suspend fun getSingleRoute(): KmbRouteEntity?

    @Query("SELECT * FROM kmb_route WHERE route = (:route) AND bound = (:bound) AND service_type = (:serviceType)")
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

//    @Query("SELECT * FROM kmb_route_stop INNER " +
//            "JOIN kmb_stop ON kmb_route_stop.stop = kmb_stop.stop " +
//            "WHERE kmb_route_stop.route = (:routeId) " +
//            "AND kmb_route_stop.bound = (:bound) " +
//            "AND kmb_route_stop.service_type = (:serviceType)")
//    suspend fun getRouteStopDetailsList(
//        routeId: String,
//        bound: Bound,
//        serviceType: String,
//    ): List<KmbRouteStopDetailsEntity>

    @Query("SELECT * FROM kmb_route_stop")
    suspend fun getRouteStopList(): List<KmbRouteStopEntity>

    @Transaction
    @Query("SELECT * FROM kmb_route_stop")
    suspend fun getRouteStopDetailsList(): List<KmbRouteStopDetails>

    @Query("SELECT * FROM kmb_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): KmbRouteStopEntity?

    @Query("SELECT * FROM kmb_route_stop WHERE route = (:route) AND bound = (:bound) AND service_type = (:serviceType)")
    suspend fun getRouteStop(
        route: String,
        bound: String,
        serviceType: String,
    ): List<KmbRouteStopEntity>

    @Insert
    suspend fun addRouteStopList(entityList: List<KmbRouteStopEntity>)

    @Query("DELETE FROM kmb_route_stop")
    suspend fun clearRouteStopList()
}
