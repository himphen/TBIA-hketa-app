package hibernate.v2.sunshine.db.nc

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class NCRouteStopComponent(
    @Embedded
    val routeStopEntity: NCRouteStopEntity,

    @Relation(
        parentColumn = "nc_route_stop_stop_id",
        entityColumn = "nc_stop_id"
    )
    val stopEntity: NCStopEntity?
)
