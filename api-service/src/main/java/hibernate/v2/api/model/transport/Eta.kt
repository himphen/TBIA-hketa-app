package hibernate.v2.api.model.transport

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class Eta(
    @SerializedName("co")
    val company: String? = null,
    @SerializedName("data_timestamp")
    val dataTimestamp: String? = null,
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

enum class Bound(val value: String) {
    @Keep
    I("inbound"),

    @Keep
    O("outbound"),

    @Keep
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

enum class Company(val value: String) {
    @Keep
    KMB("kmb"),

    @Keep
    NWFB("nwfb"),

    @Keep
    CTB("ctb"),

    @Keep
    GMB("GMB"),

    @Keep
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

enum class GmbRegion(val value: String) {
    @Keep
    HKI("HKI"),

    @Keep
    KLN("KLN"),

    @Keep
    NT("NT"),

    @Keep
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }

}