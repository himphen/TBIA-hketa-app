package hibernate.v2.database.ctb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable
import hibernatev2database.Ctb_route_stop

data class CtbRouteStopEntity(
    val routeId: String,
    var company: Company,
    val bound: Bound,
    val seq: Int,
    val stopId: String,
) : TransportHashable {
    val serviceType = "1"

    companion object {
        fun convertFrom(item: Ctb_route_stop) = CtbRouteStopEntity(
            routeId = item.ctb_route_stop_route_id,
            company = item.ctb_route_stop_company,
            bound = item.ctb_route_stop_bound,
            seq = item.ctb_route_stop_seq.toInt(),
            stopId = item.ctb_route_stop_stop_id
        )
    }

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)
}
