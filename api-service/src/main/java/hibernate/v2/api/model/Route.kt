package hibernate.v2.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(
    @SerializedName("bound")
    val bound: String? = null,
    @SerializedName("dest_en")
    val destEn: String? = null,
    @SerializedName("dest_sc")
    val destSc: String? = null,
    @SerializedName("dest_tc")
    val destTc: String? = null,
    @SerializedName("orig_en")
    val origEn: String? = null,
    @SerializedName("orig_sc")
    val origSc: String? = null,
    @SerializedName("orig_tc")
    val origTc: String? = null,
    @SerializedName("route")
    val route: String? = null,
    @SerializedName("service_type")
    val serviceType: String? = null
) : Parcelable