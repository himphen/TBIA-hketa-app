package hibernate.v2.api.model.openweather

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Daily(
    var clouds: Int? = null,
    @get:PropertyName("dew_point")
    @set:PropertyName("dew_point")
    var dewPoint: Double? = null,
    var dt: Int? = null,
    @get:PropertyName("feels_like")
    @set:PropertyName("feels_like")
    var feelsLike: FeelsLike? = null,
    var humidity: Int? = null,
    @get:PropertyName("moon_phase")
    @set:PropertyName("moon_phase")
    var moonPhase: Double? = null,
    var moonrise: Int? = null,
    var moonset: Int? = null,
    var pop: Double? = null,
    var pressure: Int? = null,
    var rain: Double? = null,
    var sunrise: Int? = null,
    var sunset: Int? = null,
    var temp: Temp? = null,
    var uvi: Double? = null,
    var weather: List<Weather>? = null,
    @get:PropertyName("wind_deg")
    @set:PropertyName("wind_deg")
    var windDeg: Int? = null,
    @get:PropertyName("wind_gust")
    @set:PropertyName("wind_gust")
    var windGust: Double? = null,
    @get:PropertyName("wind_speed")
    @set:PropertyName("wind_speed")
    var windSpeed: Double? = null
) : Parcelable