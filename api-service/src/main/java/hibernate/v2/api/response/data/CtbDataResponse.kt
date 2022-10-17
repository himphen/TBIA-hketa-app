package hibernate.v2.api.response.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.ctb.CtbRoute
import hibernate.v2.api.model.transport.ctb.CtbRouteStop
import hibernate.v2.api.model.transport.ctb.CtbStop
import hibernate.v2.api.response.BaseResponse

@Keep
data class CtbDataResponse(
    @SerializedName("route")
    val route: List<CtbRoute>? = null,
    @SerializedName("routeStop")
    val routeStop: List<CtbRouteStop>? = null,
    @SerializedName("stop")
    val stop: List<CtbStop>? = null
) : BaseResponse()
