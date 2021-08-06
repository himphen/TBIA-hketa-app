package hibernate.v2.sunshine.db.eta

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_train_eta_order")
data class TrainEtaOrderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "saved_train_eta_order_id")
    val id: Long? = null,
    val position: Int
)