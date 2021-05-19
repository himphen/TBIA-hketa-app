package hibernate.v2.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Eta(
    @SerializedName("co")
    val co: String? = null,
    @SerializedName("data_timestamp")
    val dataTimestamp: String? = null,
    @SerializedName("dest_en")
    val destEn: String? = null,
    @SerializedName("dest_sc")
    val destSc: String? = null,
    @SerializedName("dest_tc")
    val destTc: String? = null,
    @SerializedName("dir")
    val dir: String? = null,
    @SerializedName("eta")
    val eta: String? = null,
    @SerializedName("eta_seq")
    val etaSeq: Int? = null,
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
) : Parcelable