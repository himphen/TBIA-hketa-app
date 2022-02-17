package hibernate.v2.api.model.openweather

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rain(
    @get:PropertyName("1h")
    @set:PropertyName("1h")
    var h: Double? = null
) : Parcelable
