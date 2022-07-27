package hibernate.v2.sunshine.db.ctb

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class CtbRouteStopComponent(
    @Embedded
    val routeStopEntity: CtbRouteStopEntity,

    @Relation(
        parentColumn = "ctb_route_stop_stop_id",
        entityColumn = "ctb_stop_id"
    )
    val stopEntity: CtbStopEntity?
)
