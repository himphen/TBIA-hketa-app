package hibernate.v2.api.model.transport.ctb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CtbRoute(
    @SerialName("co")
    var company: Company = Company.UNKNOWN,
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
    @SerialName("route")
    var routeId: String = "",
)
