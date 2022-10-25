package hibernate.v2.api.model.transport.gmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.GmbRegion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbRouteStop(
    val bound: Bound = Bound.O,
    @SerialName("region")
    var region: GmbRegion = GmbRegion.UNKNOWN,
    @SerialName("route_id")
    var routeId: Long = 0,
    var seq: Int = 0,
    @SerialName("stop")
    var stopId: Long = 0,
    @SerialName("service_type")
    var serviceType: Long = 0,
)
