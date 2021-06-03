package hibernate.v2.sunshine.db.eta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EtaDao {
    @Query("SELECT * FROM eta_data ORDER BY routeId ASC")
    fun get(): List<EtaEntity>

    @Query("SELECT * FROM eta_data WHERE stopId=(:stopId) AND routeId=(:routeId) AND bound=(:bound) AND serviceType=(:serviceType)")
    fun get(
        stopId: String,
        routeId: String,
        bound: String,
        serviceType: String
    ): List<EtaEntity>

    @Update
    fun update(entity: EtaEntity)

    @Insert
    fun add(entity: EtaEntity)

    @Insert
    fun add(entityList: List<EtaEntity>)

    @Query("DELETE FROM eta_data WHERE stopId=(:stopId) AND routeId=(:routeId) AND bound=(:bound) AND serviceType=(:serviceType)")
    fun clear(
        stopId: String,
        routeId: String,
        bound: String,
        serviceType: String
    )

    @Query("DELETE FROM eta_data")
    fun clearAll()
}
