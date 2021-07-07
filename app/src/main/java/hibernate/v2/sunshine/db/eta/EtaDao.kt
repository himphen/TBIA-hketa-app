package hibernate.v2.sunshine.db.eta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company

@Dao
interface EtaDao {
    @Transaction
    @Query("SELECT * FROM saved_eta JOIN saved_eta_order ON saved_eta.id = saved_eta_order.id JOIN kmb_route ON saved_eta.bound = kmb_route.bound AND saved_eta.service_type = kmb_route.service_type AND saved_eta.route = kmb_route.route JOIN kmb_stop ON saved_eta.stop = kmb_stop.stop")
    suspend fun getAllKmbEtaWithOrdering(): List<EtaKmbDetails>

    @Transaction
    @Query("SELECT * FROM saved_eta JOIN saved_eta_order ON saved_eta.id = saved_eta_order.id JOIN nc_route ON saved_eta.bound = nc_route.bound AND saved_eta.route = nc_route.route JOIN nc_stop ON saved_eta.stop = nc_stop.stop")
    suspend fun getAllNCEtaWithOrdering(): List<EtaNCDetails>

    @Query("SELECT * FROM saved_eta WHERE stop=(:stopId) AND route=(:routeId) AND bound=(:bound) AND service_type=(:serviceType) AND seq=(:seq) AND company=(:company) LIMIT 1")
    suspend fun getSingleEta(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company
    ): SavedEtaEntity?

    @Update
    suspend fun update(entity: SavedEtaEntity)

    @Insert
    suspend fun add(entity: SavedEtaEntity)

    @Insert
    suspend fun add(entityList: List<SavedEtaEntity>)

    @Query("DELETE FROM saved_eta WHERE stop=(:stopId) AND route=(:routeId) AND bound=(:bound) AND service_type=(:serviceType) AND seq=(:seq)")
    suspend fun clear(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String
    )

    @Query("DELETE FROM saved_eta")
    suspend fun clearAll()
}
