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
    @Query("SELECT * FROM saved_eta JOIN saved_eta_order ON saved_eta_id = saved_eta_order_id JOIN kmb_route ON saved_eta_route_bound = kmb_route_bound AND saved_eta_service_type = kmb_route_service_type AND saved_eta_route_id = kmb_route_id JOIN kmb_stop ON saved_eta_stop_id = kmb_stop_id WHERE saved_eta_company = 'kmb'")
    suspend fun getAllKmbEtaWithOrdering(): List<EtaKmbDetails>

    @Transaction
    @Query("SELECT * FROM saved_eta JOIN saved_eta_order ON saved_eta_id = saved_eta_order_id JOIN nc_route ON saved_eta_route_bound = nc_route_bound AND saved_eta_route_id = nc_route_id JOIN nc_stop ON saved_eta_stop_id = nc_stop_id WHERE saved_eta_company = 'nwfb'")
    suspend fun getAllNCEtaWithOrdering(): List<EtaNCDetails>

    @Transaction
    @Query("SELECT * FROM saved_eta JOIN saved_eta_order ON saved_eta_id = saved_eta_order_id JOIN gmb_route ON saved_eta_route_bound = gmb_route_bound AND saved_eta_service_type = gmb_route_service_type AND saved_eta_route_id = gmb_route_id JOIN gmb_stop ON saved_eta_stop_id = gmb_stop_id WHERE saved_eta_company = 'gmb'")
    suspend fun getAllGmbEtaWithOrdering(): List<EtaGmbDetails>

    @Transaction
    @Query("SELECT * FROM saved_eta JOIN saved_eta_order ON saved_eta_id = saved_eta_order_id JOIN mtr_route ON saved_eta_route_bound = mtr_route_bound AND saved_eta_service_type = mtr_route_service_type AND saved_eta_route_id = mtr_route_id JOIN mtr_stop ON saved_eta_stop_id = mtr_stop_id WHERE saved_eta_company = 'mtr'")
    suspend fun getAllMTREtaWithOrdering(): List<EtaMTRDetails>

    @Transaction
    @Query("SELECT * FROM saved_eta JOIN saved_eta_order ON saved_eta_id = saved_eta_order_id JOIN lrt_route ON saved_eta_route_bound = lrt_route_bound AND saved_eta_service_type = lrt_route_service_type AND saved_eta_route_id = lrt_route_id JOIN lrt_stop ON saved_eta_stop_id = lrt_stop_id WHERE saved_eta_company = 'lrt'")
    suspend fun getAllLRTEtaWithOrdering(): List<EtaLRTDetails>

    @Query("SELECT * FROM saved_eta WHERE saved_eta_stop_id=(:stopId) AND saved_eta_route_id=(:routeId) AND saved_eta_route_bound=(:bound) AND saved_eta_service_type=(:serviceType) AND saved_eta_seq=(:seq) AND saved_eta_company=(:company) LIMIT 1")
    suspend fun getSingleEta(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company,
    ): SavedEtaEntity?

    @Update
    suspend fun update(entity: SavedEtaEntity)

    @Insert
    suspend fun add(entity: SavedEtaEntity)

    @Insert
    suspend fun add(entityList: List<SavedEtaEntity>)

    @Query("DELETE FROM saved_eta WHERE saved_eta_stop_id=(:stopId) AND saved_eta_route_id=(:routeId) AND saved_eta_route_bound=(:bound) AND saved_eta_service_type=(:serviceType) AND saved_eta_seq=(:seq)")
    suspend fun clear(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String,
    )

    @Query("DELETE FROM saved_eta")
    suspend fun clearAll()
}
