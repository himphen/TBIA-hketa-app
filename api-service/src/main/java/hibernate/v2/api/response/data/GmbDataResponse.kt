package hibernate.v2.api.response.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.gmb.GmbRoute
import hibernate.v2.api.model.transport.gmb.GmbRouteStop
import hibernate.v2.api.model.transport.gmb.GmbStop
import hibernate.v2.api.response.BaseResponse

@Keep
data class GmbDataResponse(
    @SerializedName("route")
    val route: List<GmbRoute>? = null,
    @SerializedName("routeStop")
    val routeStop: List<GmbRouteStop>? = null,
    @SerializedName("stop")
    val stop: List<GmbStop>? = null
) : BaseResponse()
