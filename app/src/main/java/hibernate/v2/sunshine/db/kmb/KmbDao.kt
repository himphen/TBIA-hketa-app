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

    @Query("SELECT * FROM kmb_route_stop")
    suspend fun getRouteStopList(): List<KmbRouteStopEntity>

    @Transaction
    @Query("SELECT * FROM kmb_route_stop")
    suspend fun getRouteStopDetailsList(): List<KmbRouteStopDetails>

    @Query("SELECT * FROM kmb_route_stop LIMIT 1")
    suspend fun getSingleRouteStop(): KmbRouteStopEntity?

    @Insert
    suspend fun addRouteStopList(entityList: List<KmbRouteStopEntity>)

    @Query("DELETE FROM kmb_route_stop")
    suspend fun clearRouteStopList()
}
