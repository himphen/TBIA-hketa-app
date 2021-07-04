package hibernate.v2.sunshine.db.eta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_eta_order")
data class EtaOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val position: Int
)