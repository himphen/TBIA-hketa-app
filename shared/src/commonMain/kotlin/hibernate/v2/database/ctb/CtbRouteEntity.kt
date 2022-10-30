package hibernate.v2.database.ctb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.BaseRouteEntity
import hibernate.v2.model.transport.TransportHashable
import hibernate.v2.model.transport.route.TransportRoute
import hibernatev2database.Ctb_route

data class CtbRouteEntity(
    val routeId: String,
    val bound: Bound,
    val company: Company,
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
) : TransportHashable, Comparable<CtbRouteEntity>, BaseRouteEntity() {
    val serviceType = "1"

    companion object {
        fun convertFrom(item: Ctb_route): CtbRouteEntity {
            return CtbRouteEntity(
                routeId = item.ctb_route_id,
                bound = item.ctb_route_bound,
                company = item.ctb_route_company,
                origEn = item.orig_en,
                origTc = item.orig_tc,
                origSc = item.orig_sc,
                destEn = item.dest_en,
                destTc = item.dest_tc,
                destSc = item.dest_sc
            )
        }
    }

    fun toTransportModel(): TransportRoute {
        return TransportRoute(
            routeId = routeId,
            routeNo = routeId,
            bound = bound,
            origEn = origEn,
            origTc = origTc,
            origSc = origSc,
            destEn = destEn,
            destTc = destTc,
            destSc = destSc,
            company = company,
            serviceType = "1"
        )
    }

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)

    override fun compareTo(other: CtbRouteEntity): Int {
        parseRouteNumber(routeId)
        other.parseRouteNumber(other.routeId)

        val companyCompare = company.compareTo(other.company)
        if (companyCompare != 0) return companyCompare

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        return bound.compareTo(other.bound)
    }
}
