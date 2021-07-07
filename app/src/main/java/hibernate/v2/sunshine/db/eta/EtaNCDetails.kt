package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import androidx.room.Relation
import hibernate.v2.sunshine.db.nc.NCRouteEntity
import hibernate.v2.sunshine.db.nc.NCStopEntity

data class EtaNCDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Relation(
        parentColumn = "route",
        entityColumn = "route"
    )
    val route: NCRouteEntity,
    @Relation(
        parentColumn = "stop",
        entityColumn = "stop"
    )
    val stop: NCStopEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val order: EtaOrderEntity,
)