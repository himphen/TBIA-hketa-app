package hibernate.v2.api.response.eta

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.BusEta
import hibernate.v2.api.response.BaseResponse

data class EtaResponse(
    @SerializedName("data")
    val `data`: List<BusEta>? = null
) : BaseResponse()
