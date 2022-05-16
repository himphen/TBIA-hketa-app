package hibernate.v2.sunshine.db.gmb

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class GmbRouteStopComponent(
    @Embedded
    val routeStopEntity: GmbRouteStopEntity,

    @Relation(
        parentColumn = "gmb_route_stop_stop_id",
        entityColumn = "gmb_stop_id"
    )
    val stopEntity: GmbStopEntity?
)
