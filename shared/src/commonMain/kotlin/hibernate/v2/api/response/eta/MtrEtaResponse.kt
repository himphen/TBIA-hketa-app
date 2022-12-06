package hibernate.v2.api.response.eta

import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MTREtaResponse(
    @SerialName("data")
    val data: Map<String, MTREtaRouteStop>? = null
) : BaseResponse()

@Serializable
data class MTREtaRouteStop(
    @SerialName("curr_time")
    val currTime: String? = null,
    @SerialName("sys_time")
    val sysTime: String? = null,
    @SerialName("DOWN")
    val down: List<MTREta>? = null,
    @SerialName("UP")
    val up: List<MTREta>? = null
)

@Serializable
data class MTREta(
    @SerialName("dest")
    val dest: String? = null,
    @SerialName("plat")
    val plat: String? = null,
    @SerialName("seq")
    val seq: String? = null,
    @SerialName("source")
    val source: String? = null,
    @SerialName("time")
    val time: String? = null,
    @SerialName("ttnt")
    val ttnt: String? = null,
    @SerialName("valid")
    val valid: String? = null
)
