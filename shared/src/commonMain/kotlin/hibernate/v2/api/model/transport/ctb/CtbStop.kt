package hibernate.v2.api.model.transport.ctb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CtbStop(
    /**
     * Document shows this is double type
     * However, API return string instead of double
     */
    @SerialName("la")
    var lat: String = "",
    @SerialName("lg")
    var lng: String = "",
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
