package hibernate.v2.api.response.gmb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.GmbAllRoute
import hibernate.v2.api.model.transport.GmbRoute
import hibernate.v2.api.model.transport.NCRoute
import hibernate.v2.api.response.BaseResponse

data class GmbRouteListResponse(
    @SerializedName("routes")
    val routes: GmbAllRoute
) : BaseResponse()