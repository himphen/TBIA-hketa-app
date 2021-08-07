package hibernate.v2.sunshine.model.transport

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteInfo(
    var nameEn: String,
    var nameTc: String,
    @ColorInt var color: Int,
) : Parcelable