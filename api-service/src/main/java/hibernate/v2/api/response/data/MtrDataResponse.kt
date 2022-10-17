package hibernate.v2.api.response.data

import hibernate.v2.api.model.transport.mtr.MtrRoute
import hibernate.v2.api.model.transport.mtr.MtrRouteStop
import hibernate.v2.api.model.transport.mtr.MtrStop
import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MtrDataResponse(
    @SerialName("route")
    val route: List<MtrRoute>? = null,
    @SerialName("routeStop")
    val routeStop: List<MtrRouteStop>? = null,
    @SerialName("stop")
    val stop: List<MtrStop>? = null
) : BaseResponse()
