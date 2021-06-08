package hibernate.v2.sunshine.db.eta

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import hibernate.v2.api.model.Bound
import hibernate.v2.api.request.RouteRequest
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "saved_eta")
data class EtaEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val stopId: String,
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val seq: String,
) : Parcelable {

    fun toRouteRequest() = RouteRequest(
        bound = bound,
        routeId = routeId,
        serviceType = serviceType
    )

    fun routeHashId(): String {
        return routeId + bound.value + serviceType
    }
}