package hibernate.v2.api.response.eta

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.BusEta
import hibernate.v2.api.response.BaseResponse

@Keep
data class EtaResponse(
    @SerializedName("data")
    val `data`: List<BusEta>? = null
) : BaseResponse()
