package hibernate.v2.api.response.data

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.response.BaseResponse

data class ChecksumResponse(
    @SerializedName("kmb")
    var kmb: ChecksumDetailResponse? = null,
    @SerializedName("ctb")
    var ctb: ChecksumDetailResponse? = null,
    @SerializedName("nlb")
    var nlb: ChecksumDetailResponse? = null,
    @SerializedName("mtr")
    var mtr: ChecksumDetailResponse? = null,
    @SerializedName("lrt")
    var lrt: ChecksumDetailResponse? = null,
    @SerializedName("gmb")
    var gmb: ChecksumDetailResponse? = null,
) : BaseResponse()

data class ChecksumDetailResponse(
    @SerializedName("route")
    val route: String? = null,
    @SerializedName("routeStop")
    val routeStop: String? = null,
    @SerializedName("stop")
    val stop: String? = null
) : BaseResponse()
