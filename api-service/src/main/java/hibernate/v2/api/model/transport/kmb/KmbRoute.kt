package hibernate.v2.api.model.transport.kmb

import com.google.firebase.database.PropertyName
import hibernate.v2.api.model.transport.Bound

data class KmbRoute(
    @get:PropertyName("bound")
    @set:PropertyName("bound")
    var bound: Bound = Bound.O,
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
    @get:PropertyName("service_type")
    @set:PropertyName("service_type")
    var serviceType: String = "",
)
