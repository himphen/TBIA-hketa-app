package hibernate.v2.api.model.transport

import com.google.gson.annotations.SerializedName

data class GmbAllRoute(
    @SerializedName("HKI")
    val hkiList: List<String>,
    @SerializedName("KLN")
    val klnList: List<String>,
    @SerializedName("NT")
    val ntList: List<String>,
)