package hibernate.v2.api.response.data

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.lrt.LrtRoute
import hibernate.v2.api.model.transport.lrt.LrtRouteStop
import hibernate.v2.api.model.transport.lrt.LrtStop
import hibernate.v2.api.response.BaseResponse

data class LrtDataResponse(
    @SerializedName("route")
    val route: List<LrtRoute>? = null,
    @SerializedName("routeStop")
    val routeStop: List<LrtRouteStop>? = null,
    @SerializedName("stop")
    val stop: List<LrtStop>? = null
) : BaseResponse()
