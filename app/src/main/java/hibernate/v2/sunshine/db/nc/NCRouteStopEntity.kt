package hibernate.v2.sunshine.db.nc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.nc.NCRouteStop
import hibernate.v2.sunshine.model.transport.TransportHashable

@Entity(
    tableName = "nc_route_stop",
    indices = [
        Index("nc_route_stop_stop_id"),
        Index("nc_route_stop_route_id", "nc_route_stop_bound", "nc_route_stop_company")
    ],
    primaryKeys = ["nc_route_stop_route_id", "nc_route_stop_bound", "nc_route_stop_company", "nc_route_stop_seq"]
)
data class NCRouteStopEntity(
    @ColumnInfo(name = "nc_route_stop_route_id")
    val routeId: String,
    @ColumnInfo(name = "nc_route_stop_company")
    var company: Company,
    @ColumnInfo(name = "nc_route_stop_bound")
    val bound: Bound,
    @ColumnInfo(name = "nc_route_stop_seq")
    val seq: Int,
    @ColumnInfo(name = "nc_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {
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

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)
}