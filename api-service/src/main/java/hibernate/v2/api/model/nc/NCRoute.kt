package hibernate.v2.api.model.nc

import androidx.annotation.Keep
import com.google.firebase.database.PropertyName
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.kmb.Bound

data class NCRoute(
    @PropertyName("co")
    @SerializedName("co")
    val company: Company = Company.UNKNOWN,
    @PropertyName("bound")
    @SerializedName("bound")
    val bound: Bound = Bound.OUTBOUND,
    @PropertyName("dest_en")
    @SerializedName("dest_en")
    val destEn: String = "",
    @PropertyName("dest_sc")
    @SerializedName("dest_sc")
    val destSc: String = "",
    @PropertyName("dest_tc")
    @SerializedName("dest_tc")
    val destTc: String = "",
    @PropertyName("orig_en")
    @SerializedName("orig_en")
    val origEn: String = "",
    @PropertyName("orig_sc")
    @SerializedName("orig_sc")
    val origSc: String = "",
    @PropertyName("orig_tc")
    @SerializedName("orig_tc")
    val origTc: String = "",
    @PropertyName("route")
    @SerializedName("route")
    val routeId: String = "",
)

enum class Company(val value: String) {
    @Keep
    @SerializedName("CTB")
    CTB("CTB"),

    @Keep
    @SerializedName("NWFB")
    NWFB("NWFB"),

    @Keep
    @SerializedName("UNKNOWN")
    UNKNOWN("UNKNOWN");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}