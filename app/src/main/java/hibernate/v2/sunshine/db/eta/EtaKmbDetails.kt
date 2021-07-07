package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import androidx.room.Relation
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity

data class EtaKmbDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Embedded
    val route: KmbRouteEntity,
    @Embedded
    val stop: KmbStopEntity,
    @Embedded
    val order: EtaOrderEntity,
)