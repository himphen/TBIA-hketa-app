package hibernate.v2.api.response.nlb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.nlb.NLBRouteStop
import hibernate.v2.api.response.BaseResponse

data class NLBRouteStopListResponse(
    @SerializedName("data")
    val routeStopList: List<NLBRouteStop> = listOf(),
) : BaseResponse()