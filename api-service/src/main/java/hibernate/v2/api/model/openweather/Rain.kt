package hibernate.v2.api.model.openweather

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Rain(
    @SerializedName("1h")
    var h: Double? = null
) : Parcelable
