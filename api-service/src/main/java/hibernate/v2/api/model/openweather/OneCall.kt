package hibernate.v2.api.model.openweather

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OneCall(
    var current: Current? = null,
    var daily: List<Daily>? = null,
    var hourly: List<Hourly>? = null,
    var lat: Double? = null,
    var lon: Double? = null,
    var minutely: List<Minutely>? = null,
    var timezone: String? = null,
    @SerializedName("timezone_offset")
    var timezoneOffset: Int? = null
)
