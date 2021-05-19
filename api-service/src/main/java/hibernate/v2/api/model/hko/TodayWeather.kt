package hibernate.v2.api.model.hko

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodayWeather(
    @SerializedName("humidity")
    val humidity: Humidity? = null,
    @SerializedName("icon")
    val icon: List<Int>? = null,
    @SerializedName("iconUpdateTime")
    val iconUpdateTime: String? = null,
    @SerializedName("mintempFrom00To09")
    val mintempFrom00To09: String? = null,
    @SerializedName("rainfall")
    val rainfall: Rainfall? = null,
    @SerializedName("rainfallFrom00To12")
    val rainfallFrom00To12: String? = null,
    @SerializedName("rainfallJanuaryToLastMonth")
    val rainfallJanuaryToLastMonth: String? = null,
    @SerializedName("rainfallLastMonth")
    val rainfallLastMonth: String? = null,
    @SerializedName("tcmessage")
    val tcmessage: String? = null,
    @SerializedName("temperature")
    val temperature: Temperature? = null,
    @SerializedName("updateTime")
    val updateTime: String? = null,
    @SerializedName("uvindex")
    val uvIndex: UvIndex? = null,
    @SerializedName("warningMessage")
    val warningMessage: List<String>? = null
) : Parcelable

@Parcelize
data class Humidity(
    @SerializedName("data")
    val `data`: List<HumidityData>? = null,
    @SerializedName("recordTime")
    val recordTime: String? = null
) : Parcelable

@Parcelize
data class Rainfall(
    @SerializedName("data")
    val `data`: List<RainfallData>? = null,
    @SerializedName("endTime")
    val endTime: String? = null,
    @SerializedName("startTime")
    val startTime: String? = null
) : Parcelable

@Parcelize
data class Temperature(
    @SerializedName("data")
    val `data`: List<TemperatureData>? = null,
    @SerializedName("recordTime")
    val recordTime: String? = null
) : Parcelable

@Parcelize
data class UvIndex(
    @SerializedName("data")
    val `data`: List<UvIndexData>? = null,
    @SerializedName("recordDesc")
    val recordDesc: String? = null
) : Parcelable

@Parcelize
data class HumidityData(
    @SerializedName("place")
    val place: String? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("value")
    val value: Int? = null
) : Parcelable

@Parcelize
data class RainfallData(
    @SerializedName("main")
    val main: String? = null,
    @SerializedName("max")
    val max: Int? = null,
    @SerializedName("place")
    val place: String? = null,
    @SerializedName("unit")
    val unit: String? = null
) : Parcelable

@Parcelize
data class TemperatureData(
    @SerializedName("place")
    val place: String? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("value")
    val value: Int? = null
) : Parcelable

@Parcelize
data class UvIndexData(
    @SerializedName("desc")
    val desc: String? = null,
    @SerializedName("place")
    val place: String? = null,
    @SerializedName("value")
    val value: Int? = null
) : Parcelable