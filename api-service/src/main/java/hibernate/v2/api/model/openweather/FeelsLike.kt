package hibernate.v2.api.model.openweather

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FeelsLike(
    var day: Double? = null,
    var eve: Double? = null,
    var morn: Double? = null,
    var night: Double? = null
) : Parcelable
