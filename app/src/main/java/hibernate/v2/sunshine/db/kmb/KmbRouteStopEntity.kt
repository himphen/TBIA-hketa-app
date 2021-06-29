package hibernate.v2.sunshine.db.kmb

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.Bound
import hibernate.v2.api.model.RouteStop
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "kmb_route_stop",
    indices = [Index(value = ["stop"])]
)
data class KmbRouteStopEntity(
    @ColumnInfo(name = "route")
    val routeId: String,
    val bound: Bound,
    @ColumnInfo(name = "service_type")
    val serviceType: String,
    val seq: String,
    @ColumnInfo(name = "stop")
    val stopId: String,
) : Parcelable {

    companion object {
        fun createFromApiModel(routeStop: RouteStop): KmbRouteStopEntity {
            return KmbRouteStopEntity(
                routeId = routeStop.routeId,
                bound = routeStop.bound,
                serviceType = routeStop.serviceType,
                seq = routeStop.seq,
                stopId = routeStop.stopId
            )
        }
    }
}