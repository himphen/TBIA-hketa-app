package hibernate.v2.api.model.nc

import com.google.firebase.database.PropertyName
import hibernate.v2.api.model.kmb.Bound

data class NCRouteStop(
    @get:PropertyName("co")
    @set:PropertyName("co")
    var company: Company? = null,
    @get:PropertyName("route")
    @set:PropertyName("route")
    var routeId: String = "",
    var bound: Bound = Bound.O,
    var seq: Int = 0,
    @get:PropertyName("stop")
    @set:PropertyName("stop")
    var stopId: String = "",
)