package hibernate.v2.api.model.transport.ctb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company

@Keep
data class CtbRoute(
    @SerializedName("co")
    var company: Company = Company.UNKNOWN,
    var bound: Bound = Bound.UNKNOWN,
    @SerializedName("dest_en")
    var destEn: String = "",
    @SerializedName("dest_sc")
    var destSc: String = "",
    @SerializedName("dest_tc")
    var destTc: String = "",
    @SerializedName("orig_en")
    var origEn: String = "",
    @SerializedName("orig_sc")
    var origSc: String = "",
    @SerializedName("orig_tc")
    var origTc: String = "",
    @SerializedName("route")
    var routeId: String = "",
)
