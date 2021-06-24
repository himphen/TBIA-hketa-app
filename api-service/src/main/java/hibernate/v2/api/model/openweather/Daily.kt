package hibernate.v2.api.model.openweather

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Daily(
    @PropertyName("clouds")
    val clouds: Int? = null,
    @PropertyName("dew_point")
    val dewPoint: Double? = null,
    @PropertyName("dt")
    val dt: Int? = null,
    @PropertyName("feels_like")
    val feelsLike: FeelsLike? = null,
    @PropertyName("humidity")
    val humidity: Int? = null,
    @PropertyName("moon_phase")
    val moonPhase: Double? = null,
    @PropertyName("moonrise")
    val moonrise: Int? = null,
    @PropertyName("moonset")
    val moonset: Int? = null,
    @PropertyName("pop")
    val pop: Double? = null,
    @PropertyName("pressure")
    val pressure: Int? = null,
    @PropertyName("rain")
    val rain: Double? = null,
    @PropertyName("sunrise")
    val sunrise: Int? = null,
    @PropertyName("sunset")
    val sunset: Int? = null,
    @PropertyName("temp")
    val temp: Temp? = null,
    @PropertyName("uvi")
    val uvi: Double? = null,
    @PropertyName("weather")
    val weather: List<Weather>? = null,
    @PropertyName("wind_deg")
    val windDeg: Int? = null,
    @PropertyName("wind_gust")
    val windGust: Double? = null,
    @PropertyName("wind_speed")
    val windSpeed: Double? = null
) : Parcelable