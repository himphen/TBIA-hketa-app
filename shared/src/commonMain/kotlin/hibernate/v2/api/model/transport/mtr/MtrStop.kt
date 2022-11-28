package hibernate.v2.api.model.transport.mtr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MtrStop(
    @SerialName("la")
    var lat: Double = 0.0,
    @SerialName("lg")
    var lng: Double = 0.0,
    @SerialName("ne")
    var nameEn: String = "",
    @SerialName("ns")
    var nameSc: String = "",
    @SerialName("nt")
    var nameTc: String = "",
    @SerialName("p")
    var stopId: String = "",
    @SerialName("g")
    var geohash: String = "",
)
