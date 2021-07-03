package hibernate.v2.sunshine.db.eta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hibernate.v2.api.model.Bound

@Dao
interface EtaDao {
    @Query("SELECT * FROM saved_eta " +
            "JOIN saved_eta_order ON saved_eta.id = saved_eta_order.id " +
            "JOIN kmb_route ON saved_eta.routeId = kmb_route.route " +
            "JOIN kmb_stop ON saved_eta.stopId = kmb_stop.stop " +
            "ORDER BY saved_eta_order.position ASC")
    suspend fun getAllKmbEtaWithOrdering(): List<EtaKmbDetailsEntity>

    @Query("SELECT * FROM saved_eta " +
            "JOIN saved_eta_order ON saved_eta.id = saved_eta_order.id " +
            "WHERE stopId=(:stopId) " +
            "AND routeId=(:routeId) " +
            "AND bound=(:bound) " +
            "AND serviceType=(:serviceType) " +
            "AND seq=(:seq) " +
            "AND brand=(:brand) " +
            "ORDER BY saved_eta_order.position ASC")
    suspend fun getEtaWithOrdering(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String,
        brand: Brand
    ): List<EtaEntity>

    @Query("SELECT * FROM saved_eta " +
            "JOIN saved_eta_order ON saved_eta.id = saved_eta_order.id " +
            "WHERE stopId=(:stopId) " +
            "AND routeId=(:routeId) " +
            "AND bound=(:bound) " +
            "AND serviceType=(:serviceType) " +
            "AND seq=(:seq) " +
            "AND brand=(:brand)" +
            "LIMIT 1")
    suspend fun getSingleEta(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String,
        brand: Brand
    ): EtaEntity?

    @Update
    suspend fun update(entity: EtaEntity)

    @Insert
    suspend fun add(entity: EtaEntity)

    @Insert
    suspend fun add(entityList: List<EtaEntity>)

    @Query("DELETE FROM saved_eta WHERE stopId=(:stopId) AND routeId=(:routeId) AND bound=(:bound) AND serviceType=(:serviceType) AND seq=(:seq)")
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
