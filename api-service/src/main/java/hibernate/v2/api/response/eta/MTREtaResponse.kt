package hibernate.v2.api.response.eta

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.response.BaseResponse

data class MTREtaResponse(
    @SerializedName("data")
    @Expose
    val data: Map<String, MTREtaRouteStop>? = null
) : BaseResponse()

data class MTREtaRouteStop(
    @SerializedName("curr_time")
    val currTime: String? = null,
    @SerializedName("sys_time")
    val sysTime: String? = null,
    @SerializedName("DOWN")
    val down: List<MTREta>? = null,
    @SerializedName("UP")
    val up: List<MTREta>? = null
)

data class MTREta(
    @SerializedName("dest")
    val dest: String? = null,
    @SerializedName("plat")
    val plat: String? = null,
    @SerializedName("seq")
    val seq: String? = null,
    @SerializedName("source")
    val source: String? = null,
    @SerializedName("time")
    val time: String? = null,
    @SerializedName("ttnt")
    val ttnt: String? = null,
    @SerializedName("valid")
    val valid: String? = null
)
