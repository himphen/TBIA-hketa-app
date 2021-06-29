package hibernate.v2.sunshine.db.kmb

import android.os.Parcelable
import androidx.room.Embedded
import kotlinx.parcelize.Parcelize

@Parcelize
data class KmbRouteStopDetailsEntity(
    @Embedded
    val kmbRouteStopEntity: KmbRouteStopEntity,
    @Embedded
    val kmbStopEntity: KmbStopEntity,
) : Parcelable