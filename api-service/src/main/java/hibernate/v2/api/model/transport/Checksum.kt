package hibernate.v2.api.model.transport

import com.google.gson.annotations.SerializedName

data class Checksum(
    @SerializedName("kmb")
    val kmb: CheckSumDetail? = null,
    @SerializedName("citybusNwfb")
    val citybusNwfb: CheckSumDetail? = null,
    @SerializedName("nlb")
    val nlb: CheckSumDetail? = null,
    @SerializedName("mtr")
    val mtr: CheckSumDetail? = null,
    @SerializedName("lrt")
    val lrt: CheckSumDetail? = null,
    @SerializedName("gmb")
    val gmb: CheckSumDetail? = null,
)

data class CheckSumDetail(
    @SerializedName("route")
    val route: String? = null,
    @SerializedName("routeStop")
    val routeStop: String? = null,
    @SerializedName("stop")
    val stop: String? = null
)
