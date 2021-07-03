package hibernate.v2.sunshine.db.eta

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.Bound
import hibernate.v2.api.request.RouteRequest
import hibernate.v2.sunshine.model.RouteHashable
import hibernate.v2.sunshine.model.StopHashable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "saved_eta")
data class EtaEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val brand: Brand,
    val stopId: String,
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val seq: String,
) : Parcelable, RouteHashable, StopHashable {

    fun toRouteRequest() = RouteRequest(
        bound = bound,
        routeId = routeId,
        serviceType = serviceType
    )

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