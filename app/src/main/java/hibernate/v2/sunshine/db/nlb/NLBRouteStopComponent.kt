package hibernate.v2.sunshine.db.nlb

import androidx.room.Embedded
import androidx.room.Relation

data class NLBRouteStopComponent(
    @Embedded
    val routeStopEntity: NLBRouteStopEntity,

    @Relation(
        parentColumn = "nlb_route_stop_stop_id",
        entityColumn = "nlb_stop_id"
    )
    val stopEntity: NLBStopEntity?
)
