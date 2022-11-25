package hibernate.v2.api.model.transport.gmb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbStop(
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
    var stopId: Long = 0,
    @SerialName("gh")
    var geohash: String = "",
)
