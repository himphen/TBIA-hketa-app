package hibernate.v2.sunshine.db.lrt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LRTDao {

    //////

    @Query("SELECT * FROM lrt_stop")
    suspend fun getStopList(): List<LRTStopEntity>

    @Query("SELECT * FROM lrt_stop WHERE lrt_stop_id = (:stop)")
    suspend fun getStop(stop: String): List<LRTStopEntity>

    @Query("SELECT * FROM lrt_stop LIMIT 1")
    suspend fun getSingleStop(): LRTStopEntity?

    @Insert
    suspend fun addStopList(entityList: List<LRTStopEntity>)

    @Query("DELETE FROM lrt_stop")
    suspend fun clearStopList()

    //////
}
