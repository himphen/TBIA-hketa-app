package hibernate.v2.api.model.transport.gmb

import com.google.firebase.database.PropertyName
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.GmbRegion

data class GmbRouteStop(
    val bound: Bound = Bound.O,
    @get:PropertyName("region")
    @set:PropertyName("region")
    var region: GmbRegion = GmbRegion.UNKNOWN,
    @get:PropertyName("route_id")
    @set:PropertyName("route_id")
    var routeId: Long = 0,
    var seq: Int = 0,
    @get:PropertyName("stop")
    @set:PropertyName("stop")
    var stopId: Long = 0,
    @get:PropertyName("service_type")
    @set:PropertyName("service_type")
    var serviceType: Long = 0,
)