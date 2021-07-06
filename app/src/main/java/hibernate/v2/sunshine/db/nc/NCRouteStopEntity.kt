package hibernate.v2.sunshine.db.nc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.api.model.nc.NCRouteStop
import hibernate.v2.sunshine.model.transport.RouteHashable

@Entity(
    tableName = "nc_route_stop",
    indices = [
        Index("stop"),
        Index("route", "bound")
    ],
    primaryKeys = ["route", "bound", "seq"]
)
data class NCRouteStopEntity(
    @ColumnInfo(name = "route")
    val routeId: String,
    val bound: Bound,
    val seq: Int,
    @ColumnInfo(name = "stop")
    val stopId: String,
) : RouteHashable {

    companion object {
        fun fromApiModel(routeStop: NCRouteStop) = NCRouteStopEntity(
            routeId = routeStop.routeId,
            bound = routeStop.bound,
            seq = routeStop.seq,
            stopId = routeStop.stopId
        )
    }

    override fun routeHashId() = routeId + bound.value
}