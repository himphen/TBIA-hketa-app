package hibernate.v2.api.model.transport.ctb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CtbRouteStop(
    @SerialName("c")
    var company: Company = Company.UNKNOWN,
    @SerialName("r")
    var routeId: String = "",
    @SerialName("b")
    var bound: Bound = Bound.O,
    @SerialName("s")
    var seq: Int = 0,
    @SerialName("p")
    var stopId: String = "",
)
