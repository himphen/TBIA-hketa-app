package hibernate.v2.database.mtr

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable
import hibernatev2database.Mtr_route_stop

data class MtrRouteStopEntity(
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val seq: Int,
    val stopId: String,
) : TransportHashable {

    companion object {
        fun convertFrom(item: Mtr_route_stop): MtrRouteStopEntity {
            return MtrRouteStopEntity(
                routeId = item.mtr_route_stop_route_id,
                bound = item.mtr_route_stop_bound,
                serviceType = item.mtr_route_stop_service_type,
                seq = item.mtr_route_stop_seq.toInt(),
                stopId = item.mtr_route_stop_stop_id
            )
        }
    }

    fun routeHashId() = routeHashId(Company.MTR, routeId, bound, serviceType)
}
