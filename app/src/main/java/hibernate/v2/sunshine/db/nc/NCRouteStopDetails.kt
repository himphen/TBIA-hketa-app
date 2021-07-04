package hibernate.v2.sunshine.db.nc

import androidx.room.Embedded
import androidx.room.Relation

data class NCRouteStopDetails(
    @Embedded
    val routeStopEntity: NCRouteStopEntity,

    @Relation(
        parentColumn = "stop",
        entityColumn = "stop"
    )
    val stopEntity: NCStopEntity
)