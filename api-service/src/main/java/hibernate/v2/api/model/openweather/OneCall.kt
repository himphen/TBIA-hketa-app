package hibernate.v2.api.model.openweather

import com.google.firebase.database.PropertyName

data class OneCall(
    var current: Current? = null,
    var daily: List<Daily>? = null,
    var hourly: List<Hourly>? = null,
    var lat: Double? = null,
    var lon: Double? = null,
    var minutely: List<Minutely>? = null,
    var timezone: String? = null,
    @get:PropertyName("timezone_offset")
    @set:PropertyName("timezone_offset")
    var timezoneOffset: Int? = null
)