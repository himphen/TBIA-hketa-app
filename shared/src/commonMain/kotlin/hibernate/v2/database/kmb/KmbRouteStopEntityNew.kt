package hibernate.v2.database.kmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.kmb.KmbRouteStop
import hibernate.v2.model.transport.TransportHashable
import kotlinx.serialization.SerialName

data class KmbRouteStopEntityNew(
    @SerialName("kmb_route_stop_route_id")
    val routeId: String,
    @SerialName("kmb_route_stop_bound")
    val bound: Bound,
    @SerialName("kmb_route_stop_service_type")
    val serviceType: String,
    @SerialName("kmb_route_stop_seq")
    val seq: Int,
    @SerialName("kmb_route_stop_stop_id")
    val stopId: String,
) : TransportHashable {

    companion object {
        fun fromApiModel(routeStop: KmbRouteStop): KmbRouteStopEntityNew {
            return KmbRouteStopEntityNew(
                routeId = routeStop.routeId,
                bound = routeStop.bound,
                serviceType = routeStop.serviceType,
                seq = routeStop.seq.toIntOrNull() ?: 0,
                stopId = routeStop.stopId
            )
        }
    }

    fun routeHashId() = routeHashId(Company.KMB, routeId, bound, serviceType)
}
