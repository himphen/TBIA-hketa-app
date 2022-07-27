package hibernate.v2.api.model.transport.nlb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NlbRoute(
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
    @SerializedName("route_id")
    var routeId: String = "",
    @SerializedName("route_no")
    var routeNo: String = ""
)
