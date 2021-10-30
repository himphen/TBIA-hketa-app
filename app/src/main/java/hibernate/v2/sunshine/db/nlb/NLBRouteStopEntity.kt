package hibernate.v2.sunshine.db.nlb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.transport.nlb.NLBRouteStop
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.model.transport.TransportHashable

@Entity(
    tableName = "nlb_route_stop",
    indices = [
        Index("nlb_route_stop_stop_id"),
        Index("nlb_route_stop_route_id", "nlb_route_stop_bound", "nlb_route_stop_service_type")
    ],
    primaryKeys = ["nlb_route_stop_route_id", "nlb_route_stop_bound", "nlb_route_stop_service_type", "nlb_route_stop_seq"]
)
data class NLBRouteStopEntity(
    @ColumnInfo(name = "nlb_route_stop_route_id")
    val routeId: String,
    @ColumnInfo(name = "nlb_route_stop_bound")
    val bound: Bound,
    @ColumnInfo(name = "nlb_route_stop_service_type")
    val serviceType: String,
    @ColumnInfo(name = "nlb_route_stop_seq")
    val seq: Int,
    @ColumnInfo(name = "nlb_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {

    companion object {
        fun fromApiModel(routeStop: NLBRouteStop): NLBRouteStopEntity {
            return NLBRouteStopEntity(
                routeId = routeStop.routeId,
                bound = routeStop.bound,
                serviceType = routeStop.serviceType,
                seq = routeStop.seq,
                stopId = routeStop.stopId
            )
        }
    }

    fun routeHashId() = routeHashId(Company.NLB, routeId, bound, serviceType)
}