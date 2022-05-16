package hibernate.v2.sunshine.db.eta

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "saved_eta_order")
data class EtaOrderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "saved_eta_order_id")
    val id: Long? = null,
    val position: Int
)
