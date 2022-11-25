package hibernate.v2.api.model.transport.nlb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.database.BaseRouteEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NlbRoute(
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
    var routeNo: String = ""
) : Comparable<NlbRoute>, BaseRouteEntity() {
    val bound = Bound.UNKNOWN

    val serviceType = "1"

    override fun compareTo(other: NlbRoute): Int {
        parseRouteNumber(routeNo)
        other.parseRouteNumber(other.routeNo)

        return routeComponent.compareTo(other.routeComponent)
    }
}
