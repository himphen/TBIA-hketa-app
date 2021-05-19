package hibernate.v2.api.response

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Eta
import kotlinx.parcelize.Parcelize

@Parcelize
data class EtaResponse(
    @SerializedName("data")
    val `data`: List<Eta>? = null
) : BaseResponse()