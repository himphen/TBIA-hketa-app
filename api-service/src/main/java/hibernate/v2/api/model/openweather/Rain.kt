package hibernate.v2.api.model.openweather

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rain(
    @PropertyName("1h")
    val h: Double? = null
) : Parcelable