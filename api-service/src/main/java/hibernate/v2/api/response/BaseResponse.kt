package hibernate.v2.api.response

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("generated_timestamp")
    val generatedTimestamp: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("version")
    val version: String? = null
)
