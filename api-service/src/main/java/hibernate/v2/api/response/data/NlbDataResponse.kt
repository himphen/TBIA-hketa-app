package hibernate.v2.api.response.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.nlb.NlbRoute
import hibernate.v2.api.model.transport.nlb.NlbRouteStop
import hibernate.v2.api.model.transport.nlb.NlbStop
import hibernate.v2.api.response.BaseResponse

@Keep
data class NlbDataResponse(
    @SerializedName("route")
    val route: List<NlbRoute>? = null,
    @SerializedName("routeStop")
    val routeStop: List<NlbRouteStop>? = null,
    @SerializedName("stop")
    val stop: List<NlbStop>? = null
) : BaseResponse()
