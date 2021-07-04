package hibernate.v2.api.response.kmb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.kmb.KmbStop

data class KmbStopListResponse(
    @SerializedName("data")
    val stopList: List<KmbStop>,
)