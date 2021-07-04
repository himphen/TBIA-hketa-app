package hibernate.v2.api.model.nc

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.kmb.Bound

data class NCRouteStop(
    @SerializedName("co")
    val company: Company,
    @SerializedName("route")
    val routeId: String,
    @SerializedName("dir")
    val bound: Bound,
    @SerializedName("seq")
    val seq: String,
    @SerializedName("stop")
    val stopId: String,
)