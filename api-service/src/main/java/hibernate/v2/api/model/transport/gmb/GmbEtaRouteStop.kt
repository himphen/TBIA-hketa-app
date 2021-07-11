package hibernate.v2.api.model.transport.gmb

import com.google.gson.annotations.SerializedName

data class GmbEtaRouteStop(
    @SerializedName("enabled")
    val enabled: Boolean? = null,
    @SerializedName("stop_id")
    val stopId: Int? = null,
    @SerializedName("eta")
    val etaList: List<GmbEta>? = null
)