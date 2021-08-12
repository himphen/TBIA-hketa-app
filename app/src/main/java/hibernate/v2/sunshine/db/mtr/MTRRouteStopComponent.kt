package hibernate.v2.sunshine.db.mtr

import androidx.room.Embedded
import androidx.room.Relation

data class MTRRouteStopComponent(
    @Embedded
    val routeStopEntity: MTRRouteStopEntity,

    @Relation(
        parentColumn = "mtr_route_stop_stop_id",
        entityColumn = "mtr_stop_id"
    )
    val stopEntity: MTRStopEntity?
)