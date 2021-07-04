package hibernate.v2.api.model.nc

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class NCRoute(
    @SerializedName("co")
    val company: Company,
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
)

enum class Company(val value: String) {
    @Keep
    @SerializedName("CTB")
    CTB("CTB"),

    @Keep
    @SerializedName("NWFB")
    NWFB("NWFB");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: NWFB
    }
}