package hibernate.v2.sunshine.db.nlb

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.nlb.NlbRouteStop
import hibernate.v2.sunshine.model.transport.TransportHashable

@Keep
@Entity(
    tableName = "nlb_route_stop",
    indices = [
        Index("nlb_route_stop_stop_id"),
        Index("nlb_route_stop_route_id")
    ],
    primaryKeys = ["nlb_route_stop_route_id", "nlb_route_stop_seq"]
)
data class NLBRouteStopEntity(
    @ColumnInfo(name = "nlb_route_stop_route_id")
    val routeId: String,
    @ColumnInfo(name = "nlb_route_stop_seq")
    val seq: Int,
    @ColumnInfo(name = "nlb_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {
    @Ignore
    val bound = Bound.UNKNOWN

    @Ignore
    val serviceType = "1"

    companion object {
        fun fromApiModel(routeStop: NlbRouteStop): NLBRouteStopEntity {
            return NLBRouteStopEntity(
                routeId = routeStop.routeId,
                seq = routeStop.seq,
                stopId = routeStop.stopId
            )
        }
    }

    fun routeHashId() = routeHashId(Company.NLB, routeId, bound, serviceType)
}
