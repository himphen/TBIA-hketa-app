package hibernate.v2.sunshine.db.ctb

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.ctb.CtbRouteStop
import hibernate.v2.sunshine.model.transport.TransportHashable

@Keep
@Entity(
    tableName = "ctb_route_stop",
    indices = [
        Index("ctb_route_stop_stop_id"),
        Index("ctb_route_stop_route_id", "ctb_route_stop_bound", "ctb_route_stop_company")
    ],
    primaryKeys = ["ctb_route_stop_route_id", "ctb_route_stop_bound", "ctb_route_stop_company", "ctb_route_stop_seq"]
)
data class CtbRouteStopEntity(
    @ColumnInfo(name = "ctb_route_stop_route_id")
    val routeId: String,
    @ColumnInfo(name = "ctb_route_stop_company")
    var company: Company,
    @ColumnInfo(name = "ctb_route_stop_bound")
    val bound: Bound,
    @ColumnInfo(name = "ctb_route_stop_seq")
    val seq: Int,
    @ColumnInfo(name = "ctb_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {
    @Ignore
    val serviceType = "1"

    companion object {
        fun fromApiModel(routeStop: CtbRouteStop) = CtbRouteStopEntity(
            routeId = routeStop.routeId,
            company = routeStop.company,
            bound = routeStop.bound,
            seq = routeStop.seq,
            stopId = routeStop.stopId
        )
    }

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)
}
