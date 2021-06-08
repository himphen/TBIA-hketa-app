package hibernate.v2.sunshine.db.eta

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "saved_eta_order")
data class EtaOrderEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val position: Int = 0
) : Parcelable