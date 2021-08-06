package hibernate.v2.sunshine.db.mtr

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.transport.mtr.MTRRouteStop
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.model.transport.TransportHashable

@Entity(
    tableName = "mtr_route_stop",
    indices = [
        Index("mtr_route_stop_stop_id"),
        Index("mtr_route_stop_route_id", "mtr_route_stop_bound", "mtr_route_stop_service_type")
    ],
    primaryKeys = ["mtr_route_stop_route_id", "mtr_route_stop_bound", "mtr_route_stop_service_type", "mtr_route_stop_seq"]
)
data class MTRRouteStopEntity(
    @ColumnInfo(name = "mtr_route_stop_route_id")
    val routeId: String,
    @ColumnInfo(name = "mtr_route_stop_bound")
    val bound: Bound,
    @ColumnInfo(name = "mtr_route_stop_service_type")
    val serviceType: String,
    @ColumnInfo(name = "mtr_route_stop_seq")
    val seq: Int,
    @ColumnInfo(name = "mtr_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {

    companion object {
        fun fromApiModel(routeStop: MTRRouteStop): MTRRouteStopEntity {
            return MTRRouteStopEntity(
                routeId = routeStop.routeId,
                bound = routeStop.bound,
                serviceType = routeStop.serviceType,
                seq = routeStop.seq,
                stopId = routeStop.stopId
            )
        }
    }

    fun routeHashId() = routeHashId(Company.MTR, routeId, bound, serviceType)
}