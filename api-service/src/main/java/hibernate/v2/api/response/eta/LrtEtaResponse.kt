package hibernate.v2.api.response.eta

import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LrtEtaResponse(
    @SerialName("platform_list") val platformList: List<Platform>? = null,
    @SerialName("status") val status: Int? = null,
    @SerialName("system_time") val systemTime: String? = null,
) : BaseResponse()

@Serializable
data class Platform(
    @SerialName("platform_id") val platformId: Int,
    @SerialName("route_list") val etaList: List<LRTEta>? = null,
    @SerialName("end_service_status") val endServiceStatus: Int? = null,
)

@Serializable
data class LRTEta(
    @SerialName("arrival_departure") val arrivalDeparture: ArrivalDeparture,
    @SerialName("dest_ch") val destCh: String,
    @SerialName("dest_en") val destEn: String,
    @SerialName("route_no") val routeNo: String,
    @SerialName("stop") val stop: Int,
    @SerialName("time_ch") val timeCh: String,
    @SerialName("time_en") val timeEn: String,
    @SerialName("train_length") val trainLength: Int,
)

@Serializable
enum class ArrivalDeparture(val value: String) {
    A("A"),

    D("D"),

    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}
