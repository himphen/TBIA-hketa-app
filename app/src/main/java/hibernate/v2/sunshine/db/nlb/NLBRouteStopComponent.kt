package hibernate.v2.sunshine.db.nlb

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class NlbRouteStopComponent(
    @Embedded
    val routeStopEntity: NlbRouteStopEntity,

    @Relation(
        parentColumn = "nlb_route_stop_stop_id",
        entityColumn = "nlb_stop_id"
    )
    val stopEntity: NlbStopEntity?
)
