package hibernate.v2.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stop(
    @SerializedName("lat")
    val lat: String? = null,
    @SerializedName("long")
    val long: String? = null,
    @SerializedName("name_en")
    val nameEn: String? = null,
    @SerializedName("name_sc")
    val nameSc: String? = null,
    @SerializedName("name_tc")
    val nameTc: String? = null,
    @SerializedName("stop")
    val stopId: String? = null
) : Parcelable