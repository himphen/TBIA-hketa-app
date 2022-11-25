package hibernate.v2.api.model.transport.kmb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KmbStop(
    @SerialName("la")
    var lat: String = "0.0",
    @SerialName("lg")
    var lng: String = "0.0",
    @SerialName("ne")
    var nameEn: String = "",
    @SerialName("ns")
    var nameSc: String = "",
    @SerialName("nt")
    var nameTc: String = "",
    @SerialName("p")
    var stopId: String = "",
    @SerialName("gh")
    var geohash: String = "",
)
