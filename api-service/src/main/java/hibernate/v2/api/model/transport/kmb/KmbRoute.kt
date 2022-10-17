package hibernate.v2.api.model.transport.kmb

import hibernate.v2.api.model.transport.Bound
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KmbRoute(
    @SerialName("bound")
    var bound: Bound = Bound.O,
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
    @SerialName("route")
    var routeId: String = "",
    @SerialName("service_type")
    var serviceType: String = "",
)
