package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.sunshine.db.eta.EtaEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditEta(
    val entity: EtaEntity,
    val route: TransportRoute,
    val stop: TransportStop,
) : Parcelable