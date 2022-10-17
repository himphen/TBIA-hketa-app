package hibernate.v2.api.model.transport.nlb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NlbStop(
    @SerialName("lat")
    var lat: String = "0.0",
    @SerialName("long")
    var lng: String = "0.0",
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
