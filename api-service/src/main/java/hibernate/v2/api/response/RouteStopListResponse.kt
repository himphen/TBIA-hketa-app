package hibernate.v2.api.response

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.RouteStop
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteStopListResponse(
    @SerializedName("data")
    val routeStopList: List<RouteStop>,
) : BaseResponse()