package hibernate.v2.api.response.data

import hibernate.v2.api.model.transport.ctb.CtbRoute
import hibernate.v2.api.model.transport.ctb.CtbRouteStop
import hibernate.v2.api.model.transport.ctb.CtbStop
import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CtbDataResponse(
    @SerialName("route")
    val route: List<CtbRoute>? = null,
    @SerialName("routeStop")
    val routeStop: List<CtbRouteStop>? = null,
    @SerialName("stop")
    val stop: List<CtbStop>? = null
) : BaseResponse()
