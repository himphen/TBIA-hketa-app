package hibernate.v2.api.response.nc

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.kmb.KmbRouteStop
import hibernate.v2.api.model.nc.NCRouteStop
import hibernate.v2.api.response.BaseResponse

data class NCRouteStopListResponse(
    @SerializedName("data")
    val routeStopList: List<NCRouteStop>,
) : BaseResponse()