package hibernate.v2.api.model.openweather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    var description: String? = null,
    var icon: String? = null,
    var id: Int? = null,
    var main: String? = null
) : Parcelable
