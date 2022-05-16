package hibernate.v2.api.model.openweather

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Rain(
    @get:PropertyName("1h")
    @set:PropertyName("1h")
    var h: Double? = null
) : Parcelable
