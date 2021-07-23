package hibernate.v2.sunshine.db.traffic.snapshot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.db.nc.NCStopEntity

@Dao
interface SnapshotDao {

    //////

    @Query("SELECT * FROM snapshot_camera")
    suspend fun getCameraList(): List<CameraEntity>

    @Insert
    suspend fun addCameraList(entityList: List<CameraEntity>)

    @Query("DELETE FROM snapshot_camera")
    suspend fun clearCameraList()

    @Query("SELECT * FROM snapshot_camera LIMIT 1")
    suspend fun getSingleCamera(): CameraEntity?
}
