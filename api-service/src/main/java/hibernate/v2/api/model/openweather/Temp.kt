package hibernate.v2.api.model.openweather


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Temp(
    @SerializedName("day")
    val day: Double? = null,
    @SerializedName("eve")
    val eve: Double? = null,
    @SerializedName("max")
    val max: Double? = null,
    @SerializedName("min")
    val min: Double? = null,
    @SerializedName("morn")
    val morn: Double? = null,
    @SerializedName("night")
    val night: Double? = null
) : Parcelable