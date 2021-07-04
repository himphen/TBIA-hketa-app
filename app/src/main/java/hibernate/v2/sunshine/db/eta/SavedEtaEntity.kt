package hibernate.v2.sunshine.db.eta

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.sunshine.model.transport.RouteHashable
import hibernate.v2.sunshine.model.transport.StopHashable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "saved_eta")
data class SavedEtaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val brand: Brand,
    @ColumnInfo(name = "stop")
    val stopId: String,
    @ColumnInfo(name = "route")
    val routeId: String,
    val bound: Bound,
    @ColumnInfo(name = "service_type")
    val serviceType: String,
    val seq: String,
) : Parcelable, RouteHashable, StopHashable {

    override fun routeHashId() = routeId + bound.value + serviceType

    override fun stopHashId() = routeId + bound.value + serviceType + stopId + seq
}

enum class Brand(val value: String) {
    @Keep
    KMB("kmb"),

    @Keep
    NWFB("nwfb"),

    @Keep
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}