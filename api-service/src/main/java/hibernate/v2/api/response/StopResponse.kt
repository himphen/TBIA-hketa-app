package hibernate.v2.api.response

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Stop

data class StopResponse(
    @SerializedName("data")
    val stop: Stop? = null
) : BaseResponse()