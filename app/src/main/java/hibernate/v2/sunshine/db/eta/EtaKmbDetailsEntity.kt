package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import androidx.room.Relation
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity

data class EtaKmbDetailsEntity(
    @Embedded
    val etaEntity: EtaEntity,
    @Relation(
        parentColumn = "routeId",
        entityColumn = "route"
    )
    val kmbRouteEntity: KmbRouteEntity,
    @Relation(
        parentColumn = "stopId",
        entityColumn = "stop"
    )
    val kmbStopEntity: KmbStopEntity,
)