package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import androidx.room.Relation
import hibernate.v2.sunshine.db.nc.NCRouteEntity
import hibernate.v2.sunshine.db.nc.NCStopEntity

data class EtaNCDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Embedded
    val route: NCRouteEntity,
    @Embedded
    val stop: NCStopEntity,
    @Embedded
    val order: EtaOrderEntity,
)