package hibernate.v2.api.model.transport.mtr

import hibernate.v2.api.model.transport.Bound
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MtrRouteStop(
    @SerialName("bound")
    var bound: Bound = Bound.UNKNOWN,
    @SerialName("route_id")
    var routeId: String = "",
    @SerialName("seq")
    var seq: Int = 0,
    @SerialName("service_type")
    var serviceType: String = "",
    @SerialName("stop")
    var stopId: String = "",
)
