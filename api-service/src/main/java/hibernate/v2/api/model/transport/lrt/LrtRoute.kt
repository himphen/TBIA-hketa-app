package hibernate.v2.api.model.transport.lrt

import hibernate.v2.api.model.transport.Bound
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LrtRoute(
    var bound: Bound = Bound.UNKNOWN,
    @SerialName("dest_en")
    var destEn: String = "",
    @SerialName("dest_sc")
    var destSc: String = "",
    @SerialName("dest_tc")
    var destTc: String = "",
    @SerialName("orig_en")
    var origEn: String = "",
    @SerialName("orig_sc")
    var origSc: String = "",
    @SerialName("orig_tc")
    var origTc: String = "",
    @SerialName("route_id")
    var routeId: String = "",
    @SerialName("route_info")
    var routeInfo: LrtRouteInfo = LrtRouteInfo(),
    @SerialName("service_type")
    var serviceType: String = "",
)

@Serializable
data class LrtRouteInfo(
    var color: String = "",
    @SerialName("isEnabled")
    var isEnabled: Boolean = false,
)
