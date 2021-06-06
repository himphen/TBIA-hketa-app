package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.sunshine.db.eta.EtaEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class RemoveEta(
    val entity: EtaEntity,
    val route: Route,
    val stop: Stop
) : Parcelable