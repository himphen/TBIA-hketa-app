package hibernate.v2.api.response.eta

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.gmb.GmbEtaRouteStop
import hibernate.v2.api.response.BaseResponse

data class GmbEtaResponse(
    @SerializedName("data")
    val `data`: GmbEtaRouteStop? = null
) : BaseResponse()