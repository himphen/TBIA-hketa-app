package hibernate.v2.api.model.transport.nlb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NlbRouteStop(
    @SerializedName("route_id")
    var routeId: String = "",
    @SerializedName("seq")
    var seq: Int = 0,
    @SerializedName("stop")
    var stopId: String = "",
)
