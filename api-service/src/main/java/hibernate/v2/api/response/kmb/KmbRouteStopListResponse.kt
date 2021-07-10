package hibernate.v2.api.response.kmb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.kmb.KmbRouteStop
import hibernate.v2.api.response.BaseResponse

data class KmbRouteStopListResponse(
    @SerializedName("data")
    val routeStopList: List<KmbRouteStop>,
) : BaseResponse()