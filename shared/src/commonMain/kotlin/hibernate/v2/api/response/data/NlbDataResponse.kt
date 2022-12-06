package hibernate.v2.api.response.data

import hibernate.v2.api.model.transport.nlb.NlbRoute
import hibernate.v2.api.model.transport.nlb.NlbRouteStop
import hibernate.v2.api.model.transport.nlb.NlbStop
import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NlbDataResponse(
    @SerialName("route")
    val route: List<NlbRoute>? = null,
    @SerialName("routeStop")
    val routeStop: List<NlbRouteStop>? = null,
    @SerialName("stop")
    val stop: List<NlbStop>? = null
) : BaseResponse()
