package hibernate.v2.api.model.openweather

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Minutely(
    @PropertyName("dt")
    val dt: Int? = null,
    @PropertyName("precipitation")
    val precipitation: Double? = null
) : Parcelable