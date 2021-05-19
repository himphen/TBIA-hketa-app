package hibernate.v2.api.model.openweather


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Rain(
    @SerializedName("1h")
    val h: Double? = null
) : Parcelable