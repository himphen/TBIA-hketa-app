package hibernate.v2.sunshine.db.nc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.NCRouteStop
import hibernate.v2.sunshine.model.transport.RouteHashable

@Entity(
    tableName = "nc_route_stop",
    indices = [
        Index("stop"),
        Index("route", "bound", "company")
    ],
    primaryKeys = ["route", "bound", "company", "seq"]
)
data class NCRouteStopEntity(
    @ColumnInfo(name = "route")
    val routeId: String,
    var company: Company,
    val bound: Bound,
    val seq: Int,
    @ColumnInfo(name = "stop")
    val stopId: String,
) : RouteHashable {
    @Ignore
    val serviceType = "1"

    companion object {
        fun fromApiModel(routeStop: NCRouteStop) = NCRouteStopEntity(
            routeId = routeStop.routeId,
            company = routeStop.company,
            bound = routeStop.bound,
            seq = routeStop.seq,
            stopId = routeStop.stopId
        )
    }

    override fun routeHashId() = company.value + routeId + bound.value + serviceType
}