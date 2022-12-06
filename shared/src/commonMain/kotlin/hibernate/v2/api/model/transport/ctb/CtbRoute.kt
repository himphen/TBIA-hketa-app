package hibernate.v2.api.model.transport.ctb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.BaseRouteEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CtbRoute(
    @SerialName("c")
    var company: Company = Company.UNKNOWN,
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
) : Comparable<CtbRoute>, BaseRouteEntity() {
    val serviceType = "1"

    fun isSpecialRoute(): Boolean = serviceType != "1"

    override fun compareTo(other: CtbRoute): Int {
        parseRouteNumber(routeId)
        other.parseRouteNumber(other.routeId)

        val companyCompare = company.compareTo(other.company)
        if (companyCompare != 0) return companyCompare

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        return bound.compareTo(other.bound)
    }
}

