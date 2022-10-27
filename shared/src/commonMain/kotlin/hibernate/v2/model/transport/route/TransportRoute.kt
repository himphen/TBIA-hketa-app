package hibernate.v2.model.transport.route

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable

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

    var routeComponent: RouteComponent = RouteComponent(),
) : TransportHashable, Comparable<TransportRoute> {

    private var _getDestDirectionText: String? = null

    private var _getDirectionWithRouteText: String? = null

    private var _getDirectionSubtitleText: String? = null

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)

    override fun compareTo(other: TransportRoute): Int {
        parseRouteNumber()
        other.parseRouteNumber()

        val companyCompare = company.compareTo(company)
        if (companyCompare != 0) return companyCompare

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }

    private fun parseRouteNumber() {
        if (routeComponent.parsed) return

        Regex("([A-Z]*)(\\d+)([A-Z]*)").find(routeNo)?.let { match ->
            routeComponent.routePrefix = match.destructured.component1()
            routeComponent.routeNumber = match.destructured.component2().toInt()
            routeComponent.routeSuffix = match.destructured.component3()
        }

        routeComponent.parsed = true
    }

    open fun isSpecialRoute(): Boolean = serviceType != "1"

    open fun getCardRouteText(): String = routeNo

    companion object {
        fun notFoundRoute() = TransportRoute(
            company = Company.UNKNOWN,
            routeId = "-",
            routeNo = "-",
            bound = Bound.UNKNOWN,
            serviceType = "",
            origEn = "-", // TODO
            origTc = "-", // TODO
            origSc = "-", // TODO
            destEn = "-", // TODO
            destTc = "-", // TODO
            destSc = "-",
        )
    }
}

data class RouteComponent(
    var routePrefix: String = "",
    var routeNumber: Int = 0,
    var routeSuffix: String = "",
    var parsed: Boolean = false,
) : Comparable<RouteComponent> {
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
