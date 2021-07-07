package hibernate.v2.api.response.gmb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.GmbRoute
import hibernate.v2.api.model.transport.KmbRoute
import hibernate.v2.api.response.BaseResponse

data class GmbRouteResponse(
    @SerializedName("data")
    val route: GmbRoute? = null
) : BaseResponse()