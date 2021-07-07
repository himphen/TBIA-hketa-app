package hibernate.v2.api.response.nc

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.NCRoute
import hibernate.v2.api.response.BaseResponse

data class NCRouteResponse(
    @SerializedName("data")
    val route: NCRoute? = null
) : BaseResponse()