package hibernate.v2.api.model.transport.mtr

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.database.BaseRouteEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MtrRoute(
    @SerialName("b")
    var bound: Bound = Bound.UNKNOWN,
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
    @SerialName("no")
    var routeNo: String = "",
    @SerialName("in")
    var routeInfo: MTRRouteInfo = MTRRouteInfo(),
    @SerialName("st")
    var serviceType: String = "",
) : Comparable<MtrRoute>, BaseRouteEntity() {

    override fun compareTo(other: MtrRoute): Int {
        val routeIdCompare = routeId.compareTo(other.routeId)
        if (routeIdCompare != 0) return routeIdCompare

        val boundCompare = bound.compareTo(other.bound)
        if (boundCompare != 0) return boundCompare

        return serviceType.compareTo(other.serviceType)
    }
}

@Serializable
data class MTRRouteInfo(
    @SerialName("ne")
    var nameEn: String = "",
    @SerialName("nt")
    var nameTc: String = "",
    var color: String = "",
    @SerialName("isEnabled")
    var isEnabled: Boolean = false,
)
