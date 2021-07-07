package hibernate.v2.api.response.kmb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.KmbStop
import hibernate.v2.api.response.BaseResponse

data class KmbStopResponse(
    @SerializedName("data")
    val stop: KmbStop? = null
) : BaseResponse()