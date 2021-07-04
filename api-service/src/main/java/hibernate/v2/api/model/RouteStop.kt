package hibernate.v2.api.model

import com.google.gson.annotations.SerializedName

data class RouteStop(
    @SerializedName("bound")
    val bound: Bound,
    @SerializedName("route")
    val routeId: String,
    @SerializedName("seq")
    val seq: String,
    @SerializedName("service_type")
    val serviceType: String,
    @SerializedName("stop")
    val stopId: String,
)