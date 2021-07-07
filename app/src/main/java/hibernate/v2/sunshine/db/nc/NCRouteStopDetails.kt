package hibernate.v2.sunshine.db.nc

import androidx.room.Embedded
import androidx.room.Relation

data class NCRouteStopDetails(
    @Embedded
    val routeStopEntity: NCRouteStopEntity,

    @Relation(
        parentColumn = "nc_route_stop_stop_id",
        entityColumn = "nc_stop_id"
    )
    val stopEntity: NCStopEntity?
)