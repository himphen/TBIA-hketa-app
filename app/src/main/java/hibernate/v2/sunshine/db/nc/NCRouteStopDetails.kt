package hibernate.v2.sunshine.db.nc

import androidx.room.Embedded
import androidx.room.Relation

data class NCRouteStopDetails(
    @Embedded
    val kmbRouteStopEntity: NCRouteStopEntity,

    @Relation(
        parentColumn = "stop",
        entityColumn = "stop"
    )
    val kmbStopEntity: NCStopEntity
)