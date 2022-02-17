package hibernate.v2.api.response.eta

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.nlb.NLBEta
import hibernate.v2.api.response.BaseResponse

data class NLBEtaResponse(
    @SerializedName("estimatedArrivals")
    val `data`: List<NLBEta>? = null
) : BaseResponse()
