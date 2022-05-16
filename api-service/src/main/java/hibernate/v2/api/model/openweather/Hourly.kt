package hibernate.v2.api.model.openweather

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Hourly(
    var clouds: Int? = null,
    @get:PropertyName("dew_point")
    @set:PropertyName("dew_point")
    var dewPoint: Double? = null,
    var dt: Int? = null,
    @get:PropertyName("feels_like")
    @set:PropertyName("feels_like")
    var feelsLike: Double? = null,
    var humidity: Int? = null,
    var pop: Double? = null,
    var pressure: Int? = null,
    var rain: Rain? = null,
    var temp: Double? = null,
    var uvi: Double? = null,
    var visibility: Int? = null,
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
