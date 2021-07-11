package hibernate.v2.sunshine.model.transport

import android.os.Parcelable
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import kotlinx.parcelize.Parcelize

@Parcelize
open class TransportRoute(
    open val routeId: String,
    open val routeNo: String,
    open val bound: Bound,
    open val serviceType: String,
    open val origEn: String,
    open val origTc: String,
    open val origSc: String,
    open val destEn: String,
    open val destTc: String,
    open val destSc: String,
    open val company: Company,

    var routeComponent: RouteComponent? = null,
) : TransportHashable, Comparable<TransportRoute>, Parcelable {

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)

    override fun compareTo(other: TransportRoute): Int {
        parseRouteNumber()
        other.parseRouteNumber()

        val routeCompare = routeComponent!!.compareTo(other.routeComponent!!)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }

    private fun parseRouteNumber() {
        if (routeComponent == null) {
            var routePrefix = ""
            var routeNumber = 0
            var routeSuffix = ""

            Regex("([A-Z]*)(\\d+)([A-Z]*)").find(routeNo)?.let { match ->
                routePrefix = match.destructured.component1()
                routeNumber = match.destructured.component2().toInt()
                routeSuffix = match.destructured.component3()
            }
            routeComponent = RouteComponent(
                routePrefix,
                routeNumber,
                routeSuffix
            )
        }
    }

    open fun isSpecialRoute(): Boolean = serviceType != "1"
}

@Parcelize
class RouteComponent(
    var routePrefix: String,
    var routeNumber: Int,
    var routeSuffix: String,
) : Comparable<RouteComponent>, Parcelable {
    override fun compareTo(other: RouteComponent): Int {
        val routePrefixCompare = routePrefix.compareTo(other.routePrefix)
        if (routePrefixCompare != 0) return routePrefixCompare

        val routeCompare = routeNumber.compareTo(other.routeNumber)
        if (routeCompare != 0) return routeCompare

        val routeSuffixCompare = routeSuffix.compareTo(other.routeSuffix)
        if (routeSuffixCompare != 0) return routeSuffixCompare

        return 0
    }

}