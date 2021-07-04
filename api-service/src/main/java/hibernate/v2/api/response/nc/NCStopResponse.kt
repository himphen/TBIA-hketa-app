package hibernate.v2.api.response.nc

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.kmb.KmbStop
import hibernate.v2.api.model.nc.NCStop

data class NCStopResponse(
    @SerializedName("data")
    val stop: NCStop?,
)