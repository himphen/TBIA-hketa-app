package hibernate.v2.api.response.eta

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.response.BaseResponse

@Keep
data class LrtEtaResponse(
    @SerializedName("platform_list")
    val platformList: List<Platform>? = null,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("system_time")
    val systemTime: String? = null,
) : BaseResponse()

@Keep
data class Platform(
    @SerializedName("platform_id")
    val platformId: Int,
    @SerializedName("route_list")
    val etaList: List<LRTEta>? = null,
    @SerializedName("end_service_status")
    val endServiceStatus: Int? = null,
)

@Keep
data class LRTEta(
    @SerializedName("arrival_departure")
    val arrivalDeparture: ArrivalDeparture,
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

enum class ArrivalDeparture(val value: String) {
    @Keep
    A("A"),

    @Keep
    D("D"),

    @Keep
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}
