package hibernate.v2.api.model.transport.kmb

import hibernate.v2.api.model.transport.Bound
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KmbRouteStop(
    @SerialName("b")
    var bound: Bound = Bound.O,
    @SerialName("r")
    var routeId: String = "",
    @SerialName("s")
    var seq: String = "",
    @SerialName("st")
    var serviceType: String = "",
    @SerialName("p")
    var stopId: String = "",
)
