package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.database.eta.SavedEtaEntity
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.route.TransportRoute
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookmarkEdit(
    val entity: SavedEtaEntity,
    val route: TransportRoute,
    val stop: TransportStop,
) : Parcelable
