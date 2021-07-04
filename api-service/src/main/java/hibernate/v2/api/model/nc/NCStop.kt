package hibernate.v2.api.model.nc

import com.google.gson.annotations.SerializedName

data class NCStop(
    @SerializedName("lat")
    val lat: String? = null,
    @SerializedName("long")
    val lng: String? = null,
    @SerializedName("name_en")
    val nameEn: String? = null,
    @SerializedName("name_sc")
    val nameSc: String? = null,
    @SerializedName("name_tc")
    val nameTc: String? = null,
    @SerializedName("stop")
    val stopId: String? = null,

    var seq: String? = null
) {
    fun isValid() = stopId != null
}