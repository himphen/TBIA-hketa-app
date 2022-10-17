package hibernate.v2.api.response.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.mtr.MtrRoute
import hibernate.v2.api.model.transport.mtr.MtrRouteStop
import hibernate.v2.api.model.transport.mtr.MtrStop
import hibernate.v2.api.response.BaseResponse

@Keep
data class MtrDataResponse(
    @SerializedName("route")
    val route: List<MtrRoute>? = null,
    @SerializedName("routeStop")
    val routeStop: List<MtrRouteStop>? = null,
    @SerializedName("stop")
    val stop: List<MtrStop>? = null
) : BaseResponse()
