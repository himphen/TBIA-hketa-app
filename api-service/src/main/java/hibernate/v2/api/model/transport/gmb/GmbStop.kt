package hibernate.v2.api.model.transport.gmb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GmbStop(
    @SerializedName("lat")
    var lat: Double = 0.0,
    @SerializedName("long")
    var lng: Double = 0.0,
    @SerializedName("name_en")
    var nameEn: String = "",
    @SerializedName("name_sc")
    var nameSc: String = "",
    @SerializedName("name_tc")
    var nameTc: String = "",
    @SerializedName("stop")
    var stopId: Long = 0,
    @SerializedName("geohash")
    var geohash: String = "",
)
