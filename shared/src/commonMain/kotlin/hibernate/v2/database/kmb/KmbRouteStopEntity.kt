package hibernate.v2.database.kmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable
import hibernatev2database.Kmb_route_stop
import kotlinx.serialization.SerialName

data class KmbRouteStopEntity(
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
        fun convertFrom(item: Kmb_route_stop): KmbRouteStopEntity {
            return KmbRouteStopEntity(
                routeId = item.kmb_route_stop_route_id,
                bound = item.kmb_route_stop_bound,
                serviceType = item.kmb_route_stop_service_type,
                seq = item.kmb_route_stop_seq.toInt(),
                stopId = item.kmb_route_stop_stop_id
            )
        }
    }

    fun routeHashId() = routeHashId(Company.KMB, routeId, bound, serviceType)
}
