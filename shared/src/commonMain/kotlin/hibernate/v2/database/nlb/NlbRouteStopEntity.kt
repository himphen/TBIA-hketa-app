package hibernate.v2.database.nlb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable
import hibernatev2database.Nlb_route_stop

data class NlbRouteStopEntity(
    val routeId: String,
    val seq: Int,
    val stopId: String,
) : TransportHashable {
    val bound = Bound.UNKNOWN

    val serviceType = "1"

    companion object {
        fun convertFrom(routeStop: Nlb_route_stop): NlbRouteStopEntity {
            return NlbRouteStopEntity(
                routeId = routeStop.nlb_route_stop_route_id,
                seq = routeStop.nlb_route_stop_seq.toInt(),
                stopId = routeStop.nlb_route_stop_stop_id
            )
        }
    }

    fun routeHashId() = routeHashId(Company.NLB, routeId, bound, serviceType)
}
