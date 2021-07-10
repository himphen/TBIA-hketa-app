package hibernate.v2.api.response.kmb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.response.BaseResponse

data class KmbRouteResponse(
    @SerializedName("data")
    val route: KmbRoute? = null
) : BaseResponse()