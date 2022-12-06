package hibernate.v2.api.response.eta

import hibernate.v2.api.model.transport.BusEta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// @Keep
// data class EtaResponse(
//    @SerialName("data")
//    val `data`: List<BusEta>? = null
// ) : BaseResponse()

@Serializable
data class EtaResponse(
    @SerialName("data")
    val `data`: List<BusEta>? = null
)
