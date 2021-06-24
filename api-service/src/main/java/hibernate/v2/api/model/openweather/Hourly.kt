package hibernate.v2.api.model.openweather

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Hourly(
    @PropertyName("clouds")
    val clouds: Int? = null,
    @PropertyName("dew_point")
    val dewPoint: Double? = null,
    @PropertyName("dt")
    val dt: Int? = null,
    @PropertyName("feels_like")
    val feelsLike: Double? = null,
    @PropertyName("humidity")
    val humidity: Int? = null,
    @PropertyName("pop")
    val pop: Double? = null,
    @PropertyName("pressure")
    val pressure: Int? = null,
    @PropertyName("rain")
    val rain: Rain? = null,
    @PropertyName("temp")
    val temp: Double? = null,
    @PropertyName("uvi")
    val uvi: Double? = null,
    @PropertyName("visibility")
    val visibility: Int? = null,
    @PropertyName("weather")
    val weather: List<Weather>? = null,
    @PropertyName("wind_deg")
    val windDeg: Int? = null,
    @PropertyName("wind_gust")
    val windGust: Double? = null,
    @PropertyName("wind_speed")
    val windSpeed: Double? = null
) : Parcelable