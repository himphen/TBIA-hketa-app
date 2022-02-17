package hibernate.v2.sunshine.db.lrt

import androidx.room.Embedded
import androidx.room.Relation

data class LRTRouteStopComponent(
    @Embedded
    val routeStopEntity: LRTRouteStopEntity,

    @Relation(
        parentColumn = "lrt_route_stop_stop_id",
        entityColumn = "lrt_stop_id"
    )
    val stopEntity: LRTStopEntity?,
)
