package hibernate.v2.sunshine.db.eta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EtaOrderDao {
    @Query("SELECT * FROM saved_eta_order ORDER BY position ASC")
    suspend fun get(): List<EtaOrderEntity>

    @Insert
    suspend fun add(entityList: List<EtaOrderEntity>)

    @Query("DELETE FROM saved_eta_order")
    suspend fun clearAll()

//    @Query("SELECT * FROM saved_train_eta_order ORDER BY position ASC")
//    suspend fun getTrainList(): List<TrainEtaOrderEntity>
//
//    @Insert
//    suspend fun addTrainList(entityList: List<TrainEtaOrderEntity>)
//
//    @Query("DELETE FROM saved_train_eta_order")
//    suspend fun clearTrainList()
}
