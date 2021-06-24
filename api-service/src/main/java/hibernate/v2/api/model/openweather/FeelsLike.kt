package hibernate.v2.api.model.openweather

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeelsLike(
    @PropertyName("day")
    val day: Double? = null,
    @PropertyName("eve")
    val eve: Double? = null,
    @PropertyName("morn")
    val morn: Double? = null,
    @PropertyName("night")
    val night: Double? = null
) : Parcelable