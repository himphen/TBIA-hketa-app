package hibernate.v2.api.model.transport.gmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.database.BaseRouteEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbRoute(
    val bound: Bound = Bound.O,
    @SerialName("dest_en")
    var destEn: String = "",
    @SerialName("dest_sc")
    var destSc: String = "",
    @SerialName("dest_tc")
    var destTc: String = "",
    @SerialName("orig_en")
    var origEn: String = "",
    @SerialName("orig_sc")
    var origSc: String = "",
    @SerialName("orig_tc")
    var origTc: String = "",
    @SerialName("route_id")
    var routeId: Long = 0,
    @SerialName("route_no")
    var routeNo: String = "",
    @SerialName("service_type")
    var serviceType: Long = 0,
    @SerialName("region")
    var region: GmbRegion = GmbRegion.UNKNOWN,
) : Comparable<GmbRoute>, BaseRouteEntity() {

    override fun compareTo(other: GmbRoute): Int {
        parseRouteNumber(routeNo)
        other.parseRouteNumber(routeNo)

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }
}