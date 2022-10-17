package hibernate.v2.api.response.eta

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.nlb.NLBEta
import hibernate.v2.api.response.BaseResponse

@Keep
data class NlbEtaResponse(
    @SerializedName("estimatedArrivals")
    val `data`: List<NLBEta>? = null
) : BaseResponse()
