package hibernate.v2.api.model.openweather


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Minutely(
    @SerializedName("dt")
    val dt: Int? = null,
    @SerializedName("precipitation")
    val precipitation: Int? = null
) : Parcelable