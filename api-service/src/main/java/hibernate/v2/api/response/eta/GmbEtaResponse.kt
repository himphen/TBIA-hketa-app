package hibernate.v2.api.response.eta

import hibernate.v2.api.model.transport.gmb.GmbEtaRouteStop
import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbEtaResponse(
    @SerialName("data")
    val `data`: GmbEtaRouteStop? = null
) : BaseResponse()
