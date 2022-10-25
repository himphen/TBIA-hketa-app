package hibernate.v2.api.model.transport.nlb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NlbRouteStop(
    @SerialName("route_id")
    var routeId: String = "",
    @SerialName("seq")
    var seq: Int = 0,
    @SerialName("stop")
    var stopId: String = "",
)
