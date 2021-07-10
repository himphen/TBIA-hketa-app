package hibernate.v2.api.response.nc

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.nc.NCRoute
import hibernate.v2.api.response.BaseResponse

data class NCRouteListResponse(
    @SerializedName("data")
    val routeList: List<NCRoute>
) : BaseResponse()