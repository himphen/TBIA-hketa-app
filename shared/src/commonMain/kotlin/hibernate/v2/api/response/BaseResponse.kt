package hibernate.v2.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class BaseResponse(
    @SerialName("generated_timestamp")
    val generatedTimestamp: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("version")
    val version: String? = null
)
