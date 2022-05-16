package hibernate.v2.api.model.transport.lrt

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.PropertyName
import hibernate.v2.api.model.transport.Bound
import kotlinx.parcelize.Parcelize

@Keep
data class LRTRoute(
    var bound: Bound = Bound.UNKNOWN,
    @get:PropertyName("dest_en")
    @set:PropertyName("dest_en")
    var destEn: String = "",
    @get:PropertyName("dest_sc")
    @set:PropertyName("dest_sc")
    var destSc: String = "",
    @get:PropertyName("dest_tc")
    @set:PropertyName("dest_tc")
    var destTc: String = "",
    @get:PropertyName("orig_en")
    @set:PropertyName("orig_en")
    var origEn: String = "",
    @get:PropertyName("orig_sc")
    @set:PropertyName("orig_sc")
    var origSc: String = "",
    @get:PropertyName("orig_tc")
    @set:PropertyName("orig_tc")
    var origTc: String = "",
    @get:PropertyName("route_id")
    @set:PropertyName("route_id")
    var routeId: String = "",
    @get:PropertyName("route_info")
    @set:PropertyName("route_info")
    var routeInfo: LRTRouteInfo = LRTRouteInfo(),
    @get:PropertyName("service_type")
    @set:PropertyName("service_type")
    var serviceType: String = "",
)

@Parcelize
data class LRTRouteInfo(
    var color: String = "",
    @get:PropertyName("isEnabled")
    @set:PropertyName("isEnabled")
    var isEnabled: Boolean = false,
) : Parcelable
