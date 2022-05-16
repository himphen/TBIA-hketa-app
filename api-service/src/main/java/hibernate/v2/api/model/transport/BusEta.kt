package hibernate.v2.api.model.transport

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BusEta(
    @SerializedName("co")
    val company: String? = null,
    @SerializedName("dest_en")
    val destEn: String? = null,
    @SerializedName("dest_sc")
    val destSc: String? = null,
    @SerializedName("dest_tc")
    val destTc: String? = null,
    @SerializedName("dir")
    val bound: Bound? = null,
    @SerializedName("eta")
    val eta: String? = null,
    @SerializedName("rmk_en")
    val rmkEn: String? = null,
    @SerializedName("rmk_sc")
    val rmkSc: String? = null,
    @SerializedName("rmk_tc")
    val rmkTc: String? = null,
    @SerializedName("route")
    val route: String? = null,
    @SerializedName("seq")
    val seq: Int? = null,
    @SerializedName("service_type")
    val serviceType: Int? = null
)
