package hibernate.v2.sunshine.db.kmb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.Bound
import hibernate.v2.api.model.RouteStop
import hibernate.v2.sunshine.model.transport.RouteHashable

@Entity(
    tableName = "kmb_route_stop",
    indices = [
        Index("stop"),
        Index("route", "bound", "service_type")
    ],
    primaryKeys = ["route", "bound", "service_type", "seq"]
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
) : RouteHashable {

    companion object {
        fun fromApiModel(routeStop: RouteStop): KmbRouteStopEntity {
            return KmbRouteStopEntity(
                routeId = routeStop.routeId,
                bound = routeStop.bound,
                serviceType = routeStop.serviceType,
                seq = routeStop.seq,
                stopId = routeStop.stopId
            )
        }
    }

    override fun routeHashId() = routeId + bound.value + serviceType
}