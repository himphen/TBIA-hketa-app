package hibernate.v2.api.model.transport.gmb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbEta(
    @SerialName("eta_seq")
    val seq: Int? = null,
    @SerialName("diff")
    val diff: Int? = null,
    @SerialName("timestamp")
    val timestamp: String? = null,
    @SerialName("remarks_en")
    val rmkEn: String? = null,
    @SerialName("remarks_sc")
    val rmkSc: String? = null,
    @SerialName("remarks_tc")
    val rmkTc: String? = null
)
