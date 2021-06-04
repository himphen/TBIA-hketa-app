package hibernate.v2.sunshine.db.eta

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "eta_data")
data class EtaEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val stopId: String,
    val routeId: String,
    val bound: String,
    val serviceType: String,
    val seq: Int
) : Parcelable, Comparable<EtaEntity> {

    fun routeHashId(): String {
        return routeId + bound + serviceType
    }

    override fun compareTo(other: EtaEntity): Int {
        return when {
            stopId > other.stopId -> 1
            stopId < other.stopId -> -1
            else -> 0
        }
    }
}