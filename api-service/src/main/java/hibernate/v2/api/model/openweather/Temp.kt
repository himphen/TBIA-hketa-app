package hibernate.v2.api.model.openweather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Temp(
    var day: Double? = null,
    var eve: Double? = null,
    var max: Double? = null,
    var min: Double? = null,
    var morn: Double? = null,
    var night: Double? = null
) : Parcelable
