package hibernate.v2.api.model.transport.nlb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NlbEta(
    @SerialName("estimatedArrivalTime")
    val estimatedArrivalTime: String? = null,
    @SerialName("routeVariantName")
    val routeVariantName: String? = null,
    @SerialName("departed")
    val departed: Int? = null,
    @SerialName("noGPS")
    val noGPS: Int? = null,
    @SerialName("wheelChair")
    val wheelChair: Int? = null,
    @SerialName("generateTime")
    val generateTime: String? = null
)
