package hibernate.v2.api.response.data

import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.model.transport.kmb.KmbRouteStop
import hibernate.v2.api.model.transport.kmb.KmbStop
import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KmbDataResponse(
    @SerialName("route")
    val route: List<KmbRoute>? = null,
    @SerialName("routeStop")
    val routeStop: List<KmbRouteStop>? = null,
    @SerialName("stop")
    val stop: List<KmbStop>? = null
) : BaseResponse()
