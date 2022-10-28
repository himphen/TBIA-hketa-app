package hibernate.v2.api.model.transport.kmb

import hibernate.v2.api.model.transport.Bound
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KmbRouteStop(
    @SerialName("bound")
    var bound: String = Bound.O.value,
    @SerialName("route")
    var routeId: String = "",
    @SerialName("seq")
    var seq: String = "",
    @SerialName("service_type")
    var serviceType: String = "",
    @SerialName("stop")
    var stopId: String = "",
)
