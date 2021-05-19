package hibernate.v2.api.model.openweather


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Weather(
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("main")
    val main: String? = null
) : Parcelable