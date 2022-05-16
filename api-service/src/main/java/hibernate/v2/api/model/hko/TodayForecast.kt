package hibernate.v2.api.model.hko

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class TodayForecast(
    @SerializedName("fireDangerWarning")
    val fireDangerWarning: String? = null,
    @SerializedName("forecastDesc")
    val forecastDesc: String? = null,
    @SerializedName("forecastPeriod")
    val forecastPeriod: String? = null,
    @SerializedName("generalSituation")
    val generalSituation: String? = null,
    @SerializedName("outlook")
    val outlook: String? = null,
    @SerializedName("tcInfo")
    val tcInfo: String? = null,
    @SerializedName("updateTime")
    val updateTime: String? = null
) : Parcelable
