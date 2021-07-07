package hibernate.v2.sunshine.db.kmb

import androidx.room.Embedded
import androidx.room.Relation

data class KmbRouteStopDetails(
    @Embedded
    val routeStopEntity: KmbRouteStopEntity,

    @Relation(
        parentColumn = "stop",
        entityColumn = "stop"
    )
    val stopEntity: KmbStopEntity
)