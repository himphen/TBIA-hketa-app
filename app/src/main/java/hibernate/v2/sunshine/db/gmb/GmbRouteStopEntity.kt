package hibernate.v2.sunshine.db.gmb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.gmb.GmbRouteStop
import hibernate.v2.sunshine.model.transport.TransportHashable

@Entity(
    tableName = "gmb_route_stop",
    indices = [
        Index("gmb_route_stop_stop_id"),
        Index("gmb_route_stop_route_id", "gmb_route_stop_bound", "gmb_route_stop_service_type")
    ],
    primaryKeys = ["gmb_route_stop_route_id", "gmb_route_stop_bound", "gmb_route_stop_service_type", "gmb_route_stop_seq"]
)
data class GmbRouteStopEntity(
    @ColumnInfo(name = "gmb_route_stop_route_id")
    val routeId: String,
    @ColumnInfo(name = "gmb_route_stop_bound")
    val bound: Bound,
    @ColumnInfo(name = "gmb_route_stop_service_type")
    val serviceType: String,
    @ColumnInfo(name = "gmb_route_stop_seq")
    val seq: Int,
    @ColumnInfo(name = "gmb_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {

    companion object {
        fun fromApiModel(routeStop: GmbRouteStop): GmbRouteStopEntity {
            return GmbRouteStopEntity(
                routeId = routeStop.routeId.toString(),
                bound = routeStop.bound,
                serviceType = routeStop.serviceType.toString(),
                seq = routeStop.seq,
                stopId = routeStop.stopId.toString()
            )
        }
    }

    fun routeHashId() = routeHashId(Company.GMB, routeId, bound, serviceType)
}
