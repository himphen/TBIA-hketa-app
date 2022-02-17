package hibernate.v2.api.model.transport.nlb

import com.google.gson.annotations.SerializedName

data class NLBEta(
    @SerializedName("estimatedArrivalTime")
    val estimatedArrivalTime: String? = null,
    @SerializedName("routeVariantName")
    val routeVariantName: String? = null,
    @SerializedName("departed")
    val departed: Int? = null,
    @SerializedName("noGPS")
    val noGPS: Int? = null,
    @SerializedName("wheelChair")
    val wheelChair: Int? = null,
    @SerializedName("generateTime")
    val generateTime: String? = null
)
