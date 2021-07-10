package hibernate.v2.sunshine.db.gmb

import androidx.room.Embedded
import androidx.room.Relation

data class GmbRouteStopDetails(
    @Embedded
    val routeStopEntity: GmbRouteStopEntity,

    @Relation(
        parentColumn = "gmb_route_stop_stop_id",
        entityColumn = "gmb_stop_id"
    )
    val stopEntity: GmbStopEntity?
)