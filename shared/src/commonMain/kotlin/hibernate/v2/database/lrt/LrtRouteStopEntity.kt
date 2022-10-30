package hibernate.v2.database.lrt

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable
import hibernatev2database.Lrt_route_stop

data class LrtRouteStopEntity(
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val seq: Int,
    val stopId: String,
) : TransportHashable {

    companion object {
        fun convertFrom(routeStop: Lrt_route_stop): LrtRouteStopEntity {
            return LrtRouteStopEntity(
                routeId = routeStop.lrt_route_stop_route_id,
                bound = routeStop.lrt_route_stop_bound,
                serviceType = routeStop.lrt_route_stop_service_type,
                seq = routeStop.lrt_route_stop_seq.toInt(),
                stopId = routeStop.lrt_route_stop_stop_id
            )
        }
    }

    fun routeHashId() = routeHashId(Company.LRT, routeId, bound, serviceType)
}
