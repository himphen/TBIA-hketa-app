package hibernate.v2.api.model.transport.nlb

import com.google.firebase.database.PropertyName

data class NLBRouteStop(
    @get:PropertyName("route_id")
    @set:PropertyName("route_id")
    var routeId: String = "",
    @get:PropertyName("seq")
    @set:PropertyName("seq")
    var seq: Int = 0,
    @get:PropertyName("stop")
    @set:PropertyName("stop")
    var stopId: String = "",
)
