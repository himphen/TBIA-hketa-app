package hibernate.v2.api.response

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Route

data class RouteResponse(
    @SerializedName("data")
    val route: Route? = null
) : BaseResponse()