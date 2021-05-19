package hibernate.v2.api.model.openweather


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class FeelsLike(
    @SerializedName("day")
    val day: Double? = null,
    @SerializedName("eve")
    val eve: Double? = null,
    @SerializedName("morn")
    val morn: Double? = null,
    @SerializedName("night")
    val night: Double? = null
) : Parcelable