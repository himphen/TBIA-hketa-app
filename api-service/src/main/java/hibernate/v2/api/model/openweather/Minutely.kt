package hibernate.v2.api.model.openweather

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Minutely(
    var dt: Int? = null,
    var precipitation: Double? = null
) : Parcelable
