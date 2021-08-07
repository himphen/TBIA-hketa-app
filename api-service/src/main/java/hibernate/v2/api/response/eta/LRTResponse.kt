package hibernate.v2.api.response.eta

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.response.BaseResponse

data class LRTResponse(
    @SerializedName("platform_list")
    val platformList: List<Platform>,
    @SerializedName("status")
    val status: Int,
    @SerializedName("system_time")
    val systemTime: String,
) : BaseResponse()

data class Platform(
    @SerializedName("platform_id")
    val platformId: Int,
    @SerializedName("route_list")
    val routeList: List<Route>,
)

data class Route(
    @SerializedName("arrival_departure")
    val arrivalDeparture: String,
    @SerializedName("dest_ch")
    val destCh: String,
    @SerializedName("dest_en")
    val destEn: String,
    @SerializedName("route_no")
    val routeNo: String,
    @SerializedName("stop")
    val stop: Int,
    @SerializedName("time_ch")
    val timeCh: String,
    @SerializedName("time_en")
    val timeEn: String,
    @SerializedName("train_length")
    val trainLength: Int,
)