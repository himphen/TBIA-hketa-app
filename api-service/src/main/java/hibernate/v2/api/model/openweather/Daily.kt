package hibernate.v2.api.model.openweather

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Daily(
    var clouds: Int? = null,
    @SerializedName("dew_point")
    var dewPoint: Double? = null,
    var dt: Int? = null,
    @SerializedName("feels_like")
    var feelsLike: FeelsLike? = null,
    var humidity: Int? = null,
    @SerializedName("moon_phase")
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
    @SerializedName("wind_deg")
    var windDeg: Int? = null,
    @SerializedName("wind_gust")
    var windGust: Double? = null,
    @SerializedName("wind_speed")
    var windSpeed: Double? = null
) : Parcelable
