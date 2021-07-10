package hibernate.v2.api.model.transport.kmb

import com.google.gson.annotations.SerializedName

data class KmbStop(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("long")
    val lng: Double,
    @SerializedName("name_en")
    val nameEn: String,
    @SerializedName("name_sc")
    val nameSc: String,
    @SerializedName("name_tc")
    val nameTc: String,
    @SerializedName("stop")
    val stopId: String,
)