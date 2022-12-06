package hibernate.v2.api.model.transport.gmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.database.BaseRouteEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GmbRoute(
    @SerialName("b")
    val bound: Bound = Bound.O,
    @SerialName("de")
    var destEn: String = "",
    @SerialName("ds")
    var destSc: String = "",
    @SerialName("dt")
    var destTc: String = "",
    @SerialName("oe")
    var origEn: String = "",
    @SerialName("os")
    var origSc: String = "",
    @SerialName("ot")
    var origTc: String = "",
    @SerialName("r")
    var routeId: Long = 0,
    @SerialName("no")
    var routeNo: String = "",
    @SerialName("st")
    var serviceType: Long = 0,
    @SerialName("re")
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