package hibernate.v2.sunshine.db.eta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hibernate.v2.api.model.Bound

@Dao
interface EtaDao {
    @Query("SELECT * FROM saved_eta ORDER BY routeId ASC")
    suspend fun get(): List<EtaEntity>

    @Query("SELECT * FROM saved_eta WHERE stopId=(:stopId) AND routeId=(:routeId) AND bound=(:bound) AND serviceType=(:serviceType) AND seq=(:seq)")
    fun get(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String
    ): List<EtaEntity>

    @Update
    fun update(entity: EtaEntity)

    @Insert
    fun add(entity: EtaEntity)

    @Insert
    fun add(entityList: List<EtaEntity>)

    @Query("DELETE FROM saved_eta WHERE stopId=(:stopId) AND routeId=(:routeId) AND bound=(:bound) AND serviceType=(:serviceType) AND seq=(:seq)")
    fun clear(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String
    )

    @Query("DELETE FROM saved_eta")
    fun clearAll()
}
