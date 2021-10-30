package hibernate.v2.api.response.nlb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.nlb.NLBRoute
import hibernate.v2.api.response.BaseResponse

data class NLBRouteResponse(
    @SerializedName("data")
    val route: NLBRoute? = null
) : BaseResponse()