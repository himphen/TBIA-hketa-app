package hibernate.v2.sunshine.db.lrt

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class LrtRouteStopComponent(
    @Embedded
    val routeStopEntity: LrtRouteStopEntity,

    @Relation(
        parentColumn = "lrt_route_stop_stop_id",
        entityColumn = "lrt_stop_id"
    )
    val stopEntity: LrtStopEntity?,
)
