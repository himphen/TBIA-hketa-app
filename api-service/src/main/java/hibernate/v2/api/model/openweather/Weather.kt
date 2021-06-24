package hibernate.v2.api.model.openweather

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    @PropertyName("description")
    val description: String? = null,
    @PropertyName("icon")
    val icon: String? = null,
    @PropertyName("id")
    val id: Int? = null,
    @PropertyName("main")
    val main: String? = null
) : Parcelable