package hibernate.v2.api.model.transport.nlb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NlbRouteStop(
    @SerialName("r")
    var routeId: String = "",
    @SerialName("s")
    var seq: Int = 0,
    @SerialName("p")
    var stopId: String = "",
)
