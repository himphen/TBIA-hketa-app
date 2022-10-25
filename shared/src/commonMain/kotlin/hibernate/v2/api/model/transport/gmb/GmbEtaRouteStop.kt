package hibernate.v2.api.model.transport.gmb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbEtaRouteStop(
    @SerialName("enabled")
    val enabled: Boolean? = null,
    @SerialName("stop_id")
    val stopId: Int? = null,
    @SerialName("eta")
    val etaList: List<GmbEta>? = null
)
