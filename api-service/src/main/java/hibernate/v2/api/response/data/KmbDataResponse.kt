package hibernate.v2.api.response.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.model.transport.kmb.KmbRouteStop
import hibernate.v2.api.model.transport.kmb.KmbStop
import hibernate.v2.api.response.BaseResponse

@Keep
data class KmbDataResponse(
    @SerializedName("route")
    val route: List<KmbRoute>? = null,
    @SerializedName("routeStop")
    val routeStop: List<KmbRouteStop>? = null,
    @SerializedName("stop")
    val stop: List<KmbStop>? = null
) : BaseResponse()
