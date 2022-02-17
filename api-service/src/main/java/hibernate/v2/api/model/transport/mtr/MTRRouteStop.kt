package hibernate.v2.api.model.transport.mtr

import com.google.firebase.database.PropertyName
import hibernate.v2.api.model.transport.Bound

data class MTRRouteStop(
    @get:PropertyName("bound")
    @set:PropertyName("bound")
    var bound: Bound = Bound.UNKNOWN,
    @get:PropertyName("route_id")
    @set:PropertyName("route_id")
    var routeId: String = "",
    @get:PropertyName("seq")
    @set:PropertyName("seq")
    var seq: Int = 0,
    @get:PropertyName("service_type")
    @set:PropertyName("service_type")
    var serviceType: String = "",
    @get:PropertyName("stop")
    @set:PropertyName("stop")
    var stopId: String = "",
)
