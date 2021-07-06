package hibernate.v2.api.model.kmb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class KmbRoute(
    @SerializedName("bound")
    val bound: Bound,
    @SerializedName("dest_en")
    val destEn: String,
    @SerializedName("dest_sc")
    val destSc: String,
    @SerializedName("dest_tc")
    val destTc: String,
    @SerializedName("orig_en")
    val origEn: String,
    @SerializedName("orig_sc")
    val origSc: String,
    @SerializedName("orig_tc")
    val origTc: String,
    @SerializedName("route")
    val routeId: String,
    @SerializedName("service_type")
    val serviceType: String,
)

enum class Bound(val value: String) {
    @Keep
    I("inbound"),

    @Keep
    O("outbound");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: I
    }
}