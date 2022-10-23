package hibernate.v2.sunshine.db.mtr

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class MtrRouteStopComponent(
    @Embedded
    val routeStopEntity: MtrRouteStopEntity,

    @Relation(
        parentColumn = "mtr_route_stop_stop_id",
        entityColumn = "mtr_stop_id"
    )
    val stopEntity: MtrStopEntity?
)
