package hibernate.v2.api.model.transport.nlb

import com.google.firebase.database.PropertyName
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound

data class NLBRoute(
    val bound: Bound = Bound.O,
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
    @get:PropertyName("route_id")
    @set:PropertyName("route_id")
    var routeId: Long = 0,
    @get:PropertyName("route_no")
    @set:PropertyName("route_no")
    var routeNo: String = "",
    var serviceType: Long = 0,
)