package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import androidx.room.Relation
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity

data class EtaKmbDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Relation(
        parentColumn = "route",
        entityColumn = "route"
    )
    val route: KmbRouteEntity,
    @Relation(
        parentColumn = "stop",
        entityColumn = "stop"
    )
    val stop: KmbStopEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val order: EtaOrderEntity,
)