package hibernate.v2.api.model.transport.ctb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CtbStop(
    /**
     * Document shows this is double type
     * However, API return string instead of double
     */
    @SerializedName("lat") var lat: String = "",
    @SerializedName("long")
    var lng: String = "",
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
