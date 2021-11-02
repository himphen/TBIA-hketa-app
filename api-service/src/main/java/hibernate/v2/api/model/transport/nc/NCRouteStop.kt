package hibernate.v2.api.model.transport.nc

import com.google.firebase.database.PropertyName
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company

data class NCRouteStop(
    @get:PropertyName("co")
    @set:PropertyName("co")
    var company: Company = Company.UNKNOWN,
    @get:PropertyName("route")
    @set:PropertyName("route")
    var routeId: String = "",
    @get:PropertyName("bound")
    @set:PropertyName("bound")
    var bound: Bound = Bound.O,
    @get:PropertyName("seq")
    @set:PropertyName("seq")
    var seq: Int = 0,
    @get:PropertyName("stop")
    @set:PropertyName("stop")
    var stopId: String = "",
)