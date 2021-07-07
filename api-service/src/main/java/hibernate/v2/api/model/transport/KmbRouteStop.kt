package hibernate.v2.api.model.transport

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound

data class KmbRouteStop(
    @SerializedName("bound")
    val bound: Bound,
    @SerializedName("route")
    val routeId: String,
    @SerializedName("seq")
    val seq: Int,
    @SerializedName("service_type")
    val serviceType: String,
    @SerializedName("stop")
    val stopId: String,
)