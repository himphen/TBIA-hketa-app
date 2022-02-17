package hibernate.v2.api.model.transport.gmb

import com.google.gson.annotations.SerializedName

data class GmbEta(
    @SerializedName("eta_seq")
    val seq: Int? = null,
    @SerializedName("diff")
    val diff: Int? = null,
    @SerializedName("timestamp")
    val timestamp: String? = null,
    @SerializedName("remarks_en")
    val rmkEn: String? = null,
    @SerializedName("remarks_sc")
    val rmkSc: String? = null,
    @SerializedName("remarks_tc")
    val rmkTc: String? = null
)
