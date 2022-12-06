package hibernate.v2.database.nlb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.BaseRouteEntity
import hibernate.v2.model.transport.TransportHashable
import hibernate.v2.model.transport.route.TransportRoute
import hibernatev2database.Nlb_route

data class NlbRouteEntity(
    val routeId: String,
    val routeNo: String,
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
) : TransportHashable, BaseRouteEntity() {
    val bound = Bound.UNKNOWN

    val serviceType = "1"

    fun isSpecialRoute(): Boolean = false

    companion object {
        fun convertFrom(item: Nlb_route): NlbRouteEntity {
            return NlbRouteEntity(
                routeId = item.nlb_route_id,
                routeNo = item.nlb_route_no,
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
            routeNo = routeNo,
            bound = bound,
            serviceType = serviceType,
            origEn = origEn,
            origTc = origTc,
            origSc = origSc,
            destEn = destEn,
            destTc = destTc,
            destSc = destSc,
            company = Company.NLB
        )
    }

    fun routeHashId() = routeHashId(Company.NLB, routeId, bound, serviceType)
}
