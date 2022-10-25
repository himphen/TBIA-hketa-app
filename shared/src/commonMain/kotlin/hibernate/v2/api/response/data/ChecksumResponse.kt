package hibernate.v2.api.response.data

import hibernate.v2.api.response.BaseResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChecksumResponse(
    @SerialName("kmb")
    var kmb: ChecksumDetailResponse? = null,
    @SerialName("ctb")
    var ctb: ChecksumDetailResponse? = null,
    @SerialName("nlb")
    var nlb: ChecksumDetailResponse? = null,
    @SerialName("mtr")
    var mtr: ChecksumDetailResponse? = null,
    @SerialName("lrt")
    var lrt: ChecksumDetailResponse? = null,
    @SerialName("gmb")
    var gmb: ChecksumDetailResponse? = null,
) : BaseResponse()

@Serializable
data class ChecksumDetailResponse(
    @SerialName("route")
    val route: String? = null,
    @SerialName("routeStop")
    val routeStop: String? = null,
    @SerialName("stop")
    val stop: String? = null
) : BaseResponse()
