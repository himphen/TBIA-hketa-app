package hibernate.v2.api.model.openweather


import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OneCall(
    @PropertyName("current")
    val current: Current? = null,
    @PropertyName("daily")
    val daily: List<Daily>? = null,
    @PropertyName("hourly")
    val hourly: List<Hourly>? = null,
    @PropertyName("lat")
    val lat: Double? = null,
    @PropertyName("lon")
    val lon: Double? = null,
    @PropertyName("minutely")
    val minutely: List<Minutely>? = null,
    @PropertyName("timezone")
    val timezone: String? = null,
    @PropertyName("timezone_offset")
    val timezoneOffset: Int? = null
) : Parcelable