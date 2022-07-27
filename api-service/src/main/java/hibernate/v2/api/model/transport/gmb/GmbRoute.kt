package hibernate.v2.api.model.transport.gmb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.GmbRegion

@Keep
data class GmbRoute(
    val bound: Bound = Bound.O,
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
    var routeId: Long = 0,
    @SerializedName("route_no")
    var routeNo: String = "",
    @SerializedName("service_type")
    var serviceType: Long = 0,
    @SerializedName("region")
    var region: GmbRegion = GmbRegion.UNKNOWN,
)
