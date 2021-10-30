package hibernate.v2.api.response.nlb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.nlb.NLBStop

data class NLBStopResponse(
    @SerializedName("data")
    val stop: NLBStop?,
)