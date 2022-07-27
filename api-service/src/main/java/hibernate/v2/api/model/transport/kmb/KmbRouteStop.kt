package hibernate.v2.api.model.transport.kmb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound

@Keep
data class KmbRouteStop(
    @SerializedName("bound")
    var bound: Bound = Bound.O,
    @SerializedName("route")
    var routeId: String = "",
    @SerializedName("seq")
    var seq: String = "",
    @SerializedName("service_type")
    var serviceType: String = "",
    @SerializedName("stop")
    var stopId: String = "",
)
