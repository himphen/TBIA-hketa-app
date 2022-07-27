package hibernate.v2.api.model.openweather

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Hourly(
    var clouds: Int? = null,
    @SerializedName("dew_point")
    var dewPoint: Double? = null,
    var dt: Int? = null,
    @SerializedName("feels_like")
    var feelsLike: Double? = null,
    var humidity: Int? = null,
    var pop: Double? = null,
    var pressure: Int? = null,
    var rain: Rain? = null,
    var temp: Double? = null,
    var uvi: Double? = null,
    var visibility: Int? = null,
    var weather: List<Weather>? = null,
    @SerializedName("wind_deg")
    var windDeg: Int? = null,
    @SerializedName("wind_gust")
    var windGust: Double? = null,
    @SerializedName("wind_speed")
    var windSpeed: Double? = null
) : Parcelable
