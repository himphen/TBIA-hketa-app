package hibernate.v2.api.model.transport

import com.google.gson.annotations.SerializedName

data class Checksum(
    @SerializedName("kmb")
    val kmb: String? = null,
    @SerializedName("citybusNwfb")
    val citybusNwfb: String? = null,
    @SerializedName("nlb")
    val nlb: String? = null,
    @SerializedName("mtr")
    val mtr: String? = null,
    @SerializedName("lrt")
    val lrt: String? = null,
    @SerializedName("gmb")
    val gmb: String? = null,
)