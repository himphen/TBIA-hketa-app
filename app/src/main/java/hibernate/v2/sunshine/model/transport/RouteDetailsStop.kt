package hibernate.v2.sunshine.model.transport

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteDetailsStop(
    val transportStop: TransportStop,
    var isBookmarked: Boolean
) : Parcelable
