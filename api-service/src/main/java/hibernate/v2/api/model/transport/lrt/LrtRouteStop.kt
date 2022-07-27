package hibernate.v2.api.model.transport.lrt

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound

@Keep
data class LrtRouteStop(
    @SerializedName("bound")
    var bound: Bound = Bound.UNKNOWN,
    @SerializedName("route_id")
    var routeId: String = "",
    @SerializedName("seq")
    var seq: Int = 0,
    @SerializedName("service_type")
    var serviceType: String = "",
    @SerializedName("stop")
    var stopId: String = "",
)
