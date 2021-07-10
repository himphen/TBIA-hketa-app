package hibernate.v2.api.response.kmb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.response.BaseResponse

data class KmbRouteListResponse(
    @SerializedName("data")
    val routeList: List<KmbRoute>
) : BaseResponse()