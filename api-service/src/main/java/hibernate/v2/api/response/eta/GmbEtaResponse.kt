package hibernate.v2.api.response.eta

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.gmb.GmbEtaRouteStop
import hibernate.v2.api.response.BaseResponse

@Keep
data class GmbEtaResponse(
    @SerializedName("data")
    val `data`: GmbEtaRouteStop? = null
) : BaseResponse()
