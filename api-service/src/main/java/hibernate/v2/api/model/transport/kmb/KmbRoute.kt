package hibernate.v2.api.model.transport.kmb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound

@Keep
data class KmbRoute(
    @SerializedName("bound")
    var bound: Bound = Bound.O,
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
    @SerializedName("service_type")
    var serviceType: String = "",
)
