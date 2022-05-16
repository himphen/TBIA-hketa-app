package hibernate.v2.sunshine.db.kmb

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class KmbRouteStopComponent(
    @Embedded
    val routeStopEntity: KmbRouteStopEntity,

    @Relation(
        parentColumn = "kmb_route_stop_stop_id",
        entityColumn = "kmb_stop_id"
    )
    val stopEntity: KmbStopEntity?
)
