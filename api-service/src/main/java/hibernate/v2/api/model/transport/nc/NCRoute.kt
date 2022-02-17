package hibernate.v2.api.model.transport.nc

import com.google.firebase.database.PropertyName
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company

data class NCRoute(
    @get:PropertyName("co")
    @set:PropertyName("co")
    var company: Company = Company.UNKNOWN,
    var bound: Bound = Bound.UNKNOWN,
    @get:PropertyName("dest_en")
    @set:PropertyName("dest_en")
    var destEn: String = "",
    @get:PropertyName("dest_sc")
    @set:PropertyName("dest_sc")
    var destSc: String = "",
    @get:PropertyName("dest_tc")
    @set:PropertyName("dest_tc")
    var destTc: String = "",
    @get:PropertyName("orig_en")
    @set:PropertyName("orig_en")
    var origEn: String = "",
    @get:PropertyName("orig_sc")
    @set:PropertyName("orig_sc")
    var origSc: String = "",
    @get:PropertyName("orig_tc")
    @set:PropertyName("orig_tc")
    var origTc: String = "",
    @get:PropertyName("route")
    @set:PropertyName("route")
    var routeId: String = "",
)
