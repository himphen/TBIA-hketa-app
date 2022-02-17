package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditEta(
    val entity: SavedEtaEntity,
    val route: TransportRoute,
    val stop: TransportStop,
) : Parcelable
