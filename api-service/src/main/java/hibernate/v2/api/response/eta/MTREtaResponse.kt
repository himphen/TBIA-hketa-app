package hibernate.v2.api.response.eta

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.response.BaseResponse

data class MTREtaResponse(
    @SerializedName("data")
    @Expose
    val result: Map<String, MTREtaRouteStop>
) : BaseResponse()

data class MTREtaRouteStop(
    @SerializedName("curr_time")
    val currTime: String? = null,
    @SerializedName("sys_time")
    val sysTime: String? = null,
    @SerializedName("DOWN")
    val dOWN: List<MTREta>? = null,
    @SerializedName("UP")
    val uP: List<MTREta>? = null
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