package hibernate.v2.sunshine.db.lrt

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.lrt.LRTRouteStop
import hibernate.v2.sunshine.model.transport.TransportHashable

@Keep
@Entity(
    tableName = "lrt_route_stop",
    indices = [
        Index("lrt_route_stop_stop_id"),
        Index("lrt_route_stop_route_id", "lrt_route_stop_bound", "lrt_route_stop_service_type")
    ],
    primaryKeys = ["lrt_route_stop_route_id", "lrt_route_stop_bound", "lrt_route_stop_service_type", "lrt_route_stop_seq"]
)
data class LRTRouteStopEntity(
    @ColumnInfo(name = "lrt_route_stop_route_id")
    val routeId: String,
    @ColumnInfo(name = "lrt_route_stop_bound")
    val bound: Bound,
    @ColumnInfo(name = "lrt_route_stop_service_type")
    val serviceType: String,
    @ColumnInfo(name = "lrt_route_stop_seq")
    val seq: Int,
    @ColumnInfo(name = "lrt_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {

    companion object {
        fun fromApiModel(routeStop: LRTRouteStop): LRTRouteStopEntity {
            return LRTRouteStopEntity(
                routeId = routeStop.routeId,
                bound = routeStop.bound,
                serviceType = routeStop.serviceType,
                seq = routeStop.seq,
                stopId = routeStop.stopId
            )
        }
    }

    fun routeHashId() = routeHashId(Company.LRT, routeId, bound, serviceType)
}
