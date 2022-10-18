package hibernate.v2.api.response.eta

import hibernate.v2.api.model.transport.nlb.NlbEta
import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NlbEtaResponse(
    @SerialName("estimatedArrivals")
    val `data`: List<NlbEta>? = null
) : BaseResponse()
