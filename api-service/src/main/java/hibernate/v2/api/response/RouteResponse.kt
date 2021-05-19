package hibernate.v2.api.response

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Eta
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteResponse(
    @SerializedName("data")
    val route: Route? = null
) : BaseResponse()