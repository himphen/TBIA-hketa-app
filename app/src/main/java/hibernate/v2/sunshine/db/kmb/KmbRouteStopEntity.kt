package hibernate.v2.sunshine.db.kmb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.transport.KmbRouteStop
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.model.transport.TransportHashable

@Entity(
    tableName = "kmb_route_stop",
    indices = [
        Index("kmb_route_stop_stop_id"),
        Index("kmb_route_stop_route_id", "kmb_route_stop_bound", "kmb_route_stop_service_type")
    ],
    primaryKeys = ["kmb_route_stop_route_id", "kmb_route_stop_bound", "kmb_route_stop_service_type", "kmb_route_stop_seq"]
)
data class KmbRouteStopEntity(
    @ColumnInfo(name = "kmb_route_stop_route_id")
    val routeId: String,
    @ColumnInfo(name = "kmb_route_stop_bound")
    val bound: Bound,
    @ColumnInfo(name = "kmb_route_stop_service_type")
    val serviceType: String,
    @ColumnInfo(name = "kmb_route_stop_seq")
    val seq: Int,
    @ColumnInfo(name = "kmb_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {

    companion object {
        fun fromApiModel(routeStop: KmbRouteStop): KmbRouteStopEntity {
            return KmbRouteStopEntity(
                routeId = routeStop.routeId,
                bound = routeStop.bound,
                serviceType = routeStop.serviceType,
                seq = routeStop.seq,
                stopId = routeStop.stopId
            )
        }
    }

    fun routeHashId() = routeHashId(Company.KMB, routeId, bound, serviceType)
}