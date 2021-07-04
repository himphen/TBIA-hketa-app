package hibernate.v2.sunshine.db.nc

import androidx.room.Embedded
import androidx.room.Relation

data class NCRouteWithRouteStop(
    val entityList: List<NCRouteStopEntity>,

    @Relation(
        parentColumn = "stop",
        entityColumn = "stop"
    )
    val routeEntity: NCRouteEntity
)