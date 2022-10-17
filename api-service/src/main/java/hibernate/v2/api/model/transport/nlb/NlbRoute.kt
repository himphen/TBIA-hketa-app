package hibernate.v2.api.model.transport.nlb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NlbRoute(
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
    @SerialName("route_no")
    var routeNo: String = ""
)
