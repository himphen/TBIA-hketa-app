package hibernate.v2.api.model.transport.gmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.GmbRegion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbRouteStop(
    val bound: Bound = Bound.O,
    @SerialName("re")
    var region: GmbRegion = GmbRegion.UNKNOWN,
    @SerialName("r")
    var routeId: Long = 0,
    @SerialName("s")
    var seq: Int = 0,
    @SerialName("p")
    var stopId: Long = 0,
    @SerialName("st")
    var serviceType: Long = 0,
)
