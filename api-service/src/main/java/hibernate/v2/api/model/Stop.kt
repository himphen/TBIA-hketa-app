package hibernate.v2.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stop(
    @SerializedName("lat")
    val lat: String,
    @SerializedName("long")
    val lng: String,
    @SerializedName("name_en")
    val nameEn: String,
    @SerializedName("name_sc")
    val nameSc: String,
    @SerializedName("name_tc")
    val nameTc: String,
    @SerializedName("stop")
    val stopId: String,

    var seq: String? = null
) : Parcelable