package hibernate.v2.api.model.openweather

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Weather(
    var description: String? = null,
    var icon: String? = null,
    var id: Int? = null,
    var main: String? = null
) : Parcelable
