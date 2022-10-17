package hibernate.v2.api.response.data

import hibernate.v2.api.model.transport.lrt.LrtRoute
import hibernate.v2.api.model.transport.lrt.LrtRouteStop
import hibernate.v2.api.model.transport.lrt.LrtStop
import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LrtDataResponse(
    @SerialName("route")
    val route: List<LrtRoute>? = null,
    @SerialName("routeStop")
    val routeStop: List<LrtRouteStop>? = null,
    @SerialName("stop")
    val stop: List<LrtStop>? = null
) : BaseResponse()
