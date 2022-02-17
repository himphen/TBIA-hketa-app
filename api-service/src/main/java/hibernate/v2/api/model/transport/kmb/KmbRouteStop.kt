package hibernate.v2.api.model.transport.kmb

import com.google.firebase.database.PropertyName
import hibernate.v2.api.model.transport.Bound

data class KmbRouteStop(
    @get:PropertyName("bound")
    @set:PropertyName("bound")
    var bound: Bound = Bound.O,
    @get:PropertyName("route")
    @set:PropertyName("route")
    var routeId: String = "",
    @get:PropertyName("seq")
    @set:PropertyName("seq")
    var seq: String = "",
    @get:PropertyName("service_type")
    @set:PropertyName("service_type")
    var serviceType: String = "",
    @get:PropertyName("stop")
    @set:PropertyName("stop")
    var stopId: String = "",
)
