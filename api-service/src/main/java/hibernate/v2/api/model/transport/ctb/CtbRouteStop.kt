package hibernate.v2.api.model.transport.ctb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company

@Keep
data class CtbRouteStop(
    @SerializedName("co")
    var company: Company = Company.UNKNOWN,
    @SerializedName("route")
    var routeId: String = "",
    @SerializedName("bound")
    var bound: Bound = Bound.O,
    @SerializedName("seq")
    var seq: Int = 0,
    @SerializedName("stop")
    var stopId: String = "",
)
