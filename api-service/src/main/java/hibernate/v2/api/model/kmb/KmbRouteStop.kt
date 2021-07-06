package hibernate.v2.api.model.kmb

import com.google.gson.annotations.SerializedName

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