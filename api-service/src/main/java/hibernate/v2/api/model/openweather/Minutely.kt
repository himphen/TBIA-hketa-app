package hibernate.v2.api.model.openweather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Minutely(
    var dt: Int? = null,
    var precipitation: Double? = null
) : Parcelable
