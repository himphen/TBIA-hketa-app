package hibernate.v2.database.gmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable
import hibernatev2database.Gmb_route_stop

data class GmbRouteStopEntity(
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val seq: Int,
    val stopId: String,
) : TransportHashable {

    companion object {
        fun convertFrom(item: Gmb_route_stop): GmbRouteStopEntity {
            return GmbRouteStopEntity(
                routeId = item.gmb_route_stop_route_id,
                bound = item.gmb_route_stop_bound,
                serviceType = item.gmb_route_stop_service_type,
                seq = item.gmb_route_stop_seq.toInt(),
                stopId = item.gmb_route_stop_stop_id
            )
        }
    }

    fun routeHashId() = routeHashId(Company.GMB, routeId, bound, serviceType)
}
