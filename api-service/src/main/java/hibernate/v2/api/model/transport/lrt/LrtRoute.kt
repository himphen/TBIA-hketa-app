package hibernate.v2.api.model.transport.lrt

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound
import kotlinx.parcelize.Parcelize

@Keep
data class LrtRoute(
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
    @SerializedName("route_id")
    var routeId: String = "",
    @SerializedName("route_info")
    var routeInfo: LRTRouteInfo = LRTRouteInfo(),
    @SerializedName("service_type")
    var serviceType: String = "",
)

@Parcelize
data class LRTRouteInfo(
    var color: String = "",
    @SerializedName("isEnabled")
    var isEnabled: Boolean = false,
) : Parcelable
