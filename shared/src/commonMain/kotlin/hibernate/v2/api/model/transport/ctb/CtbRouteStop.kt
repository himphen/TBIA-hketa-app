package hibernate.v2.api.model.transport.ctb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CtbRouteStop(
    @SerialName("co")
    var company: Company = Company.UNKNOWN,
    @SerialName("route")
    var routeId: String = "",
    @SerialName("bound")
    var bound: Bound = Bound.O,
    @SerialName("seq")
    var seq: Int = 0,
    @SerialName("stop")
    var stopId: String = "",
)
