package hibernate.v2.sunshine.model.transport

import android.os.Parcelable
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransportRoute(
    val routeId: String,
    val bound: Bound,
    val serviceType: String = "1",
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
    val company: Company,

    var routeParsed: Boolean = false,
    var routePrefix: String? = null,
    var routeNumber: Int? = null,
    var routeSuffix: String? = null,
) : Parcelable, TransportHashable, Comparable<TransportRoute> {

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)

    override fun compareTo(other: TransportRoute): Int {
        parseRouteNumber()
        other.parseRouteNumber()

        val routePrefixCompare = routePrefix!!.compareTo(other.routePrefix!!)
        if (routePrefixCompare != 0) return routePrefixCompare

        val routeCompare = routeNumber!!.compareTo(other.routeNumber!!)
        if (routeCompare != 0) return routeCompare

        val routeSuffixCompare = routeSuffix!!.compareTo(other.routeSuffix!!)
        if (routeSuffixCompare != 0) return routeSuffixCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }

    private fun parseRouteNumber() {
        if (!routeParsed) {
            Regex("([A-Z]*)(\\d+)([A-Z]*)").find(routeId)?.let { match ->
                routePrefix = match.destructured.component1()
                routeNumber = match.destructured.component2().toInt()
                routeSuffix = match.destructured.component3()
            }
            routeParsed = true
        }
    }

    fun isSpecialRoute(): Boolean = serviceType != "1"
}