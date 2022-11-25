package hibernate.v2.api.model.transport.kmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.database.BaseRouteEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KmbRoute(
    @SerialName("b")
    var bound: String = Bound.O.value,
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
    var routeId: String = "",
    @SerialName("st")
    var serviceType: String = "",
) : Comparable<KmbRoute>, BaseRouteEntity() {

    fun isSpecialRoute(): Boolean = serviceType != "1"

    override fun compareTo(other: KmbRoute): Int {
        parseRouteNumber(routeId)
        other.parseRouteNumber(other.routeId)

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }
}
