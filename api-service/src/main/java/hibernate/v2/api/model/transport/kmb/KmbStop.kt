package hibernate.v2.api.model.transport.kmb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class KmbStop(
    @SerializedName("lat")
    var lat: String = "0.0",
    @SerializedName("long")
    var lng: String = "0.0",
    @SerializedName("name_en")
    var nameEn: String = "",
    @SerializedName("name_sc")
    var nameSc: String = "",
    @SerializedName("name_tc")
    var nameTc: String = "",
    @SerializedName("stop")
    var stopId: String = "",
    @SerializedName("geohash")
    var geohash: String = "",
)
