package hibernate.v2.api.response

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Stop

data class StopListResponse(
    @SerializedName("data")
    val stopList: List<Stop>,
)