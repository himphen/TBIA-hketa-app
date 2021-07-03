package hibernate.v2.sunshine.db.kmb

import androidx.room.Embedded
import androidx.room.Relation

data class KmbRouteStopDetails(
    @Embedded
    val kmbRouteStopEntity: KmbRouteStopEntity,

    @Relation(
        parentColumn = "stop",
        entityColumn = "stop"
    )
    val kmbStopEntity: KmbStopEntity
)