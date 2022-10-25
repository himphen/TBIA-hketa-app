package hibernate.v2.api.response.data

import hibernate.v2.api.model.transport.gmb.GmbRoute
import hibernate.v2.api.model.transport.gmb.GmbRouteStop
import hibernate.v2.api.model.transport.gmb.GmbStop
import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbDataResponse(
    @SerialName("route")
    val route: List<GmbRoute>? = null,
    @SerialName("routeStop")
    val routeStop: List<GmbRouteStop>? = null,
    @SerialName("stop")
    val stop: List<GmbStop>? = null
) : BaseResponse()
