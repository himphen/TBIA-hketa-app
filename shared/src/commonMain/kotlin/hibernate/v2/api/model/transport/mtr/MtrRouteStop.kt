package hibernate.v2.api.model.transport.mtr

import hibernate.v2.api.model.transport.Bound
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MtrRouteStop(
    @SerialName("b")
    var bound: Bound = Bound.UNKNOWN,
    @SerialName("r")
    var routeId: String = "",
    @SerialName("s")
    var seq: Int = 0,
    @SerialName("st")
    var serviceType: String = "",
    @SerialName("p")
    var stopId: String = "",
)
