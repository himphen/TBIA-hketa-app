package hibernate.v2.api.model.transport.gmb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbStop(
    @SerialName("lat")
    var lat: Double = 0.0,
    @SerialName("long")
    var lng: Double = 0.0,
    @SerialName("name_en")
    var nameEn: String = "",
    @SerialName("name_sc")
    var nameSc: String = "",
    @SerialName("name_tc")
    var nameTc: String = "",
    @SerialName("stop")
    var stopId: Long = 0,
    @SerialName("geohash")
    var geohash: String = "",
)
