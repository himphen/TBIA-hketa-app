package hibernate.v2.api.model.transport.ctb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CtbStop(
    /**
     * Document shows this is double type
     * However, API return string instead of double
     */
    @SerialName("lat") var lat: String = "",
    @SerialName("long")
    var lng: String = "",
    @SerialName("name_en")
    var nameEn: String = "",
    @SerialName("name_sc")
    var nameSc: String = "",
    @SerialName("name_tc")
    var nameTc: String = "",
    @SerialName("stop")
    var stopId: String = "",
    @SerialName("geohash")
    var geohash: String = "",
)
