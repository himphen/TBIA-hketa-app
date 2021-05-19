package hibernate.v2.api.model.openweather


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class OneCall(
    @SerializedName("current")
    val current: Current? = null,
    @SerializedName("daily")
    val daily: List<Daily>? = null,
    @SerializedName("hourly")
    val hourly: List<Hourly>? = null,
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lon")
    val lon: Double? = null,
    @SerializedName("minutely")
    val minutely: List<Minutely>? = null,
    @SerializedName("timezone")
    val timezone: String? = null,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int? = null
) : Parcelable