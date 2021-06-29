package hibernate.v2.sunshine.db.kmb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hibernate.v2.api.model.Bound

@Dao
interface KmbDao {

    //////

    @Query("SELECT * FROM kmb_stop")
    suspend fun getStopList(): List<KmbStopEntity>

    suspend fun getStop(stop: String): KmbStopEntity?

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

    @Query("SELECT * FROM kmb_route_stop INNER JOIN kmb_stop ON kmb_route_stop.stop = kmb_stop.stop WHERE kmb_route_stop.route = (:routeId) AND kmb_route_stop.bound = (:bound) AND kmb_route_stop.service_type = (:serviceType)")
    suspend fun getRouteStopDetailsList(
        routeId: String,
        bound: Bound,
        serviceType: String,
    ): List<KmbRouteStopDetailsEntity>

    @Query("SELECT * FROM kmb_route_stop")
    suspend fun getRouteStopList(): List<KmbRouteStopEntity>

    @Query("SELECT * FROM kmb_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): KmbRouteStopEntity?

    suspend fun getRouteStop(
        route: String,
        bound: String,
        serviceType: String,
    ): KmbRouteStopEntity?

    @Insert
    suspend fun addRouteStopList(entityList: List<KmbRouteStopEntity>)

    @Query("DELETE FROM kmb_route_stop")
    suspend fun clearRouteStopList()
}
