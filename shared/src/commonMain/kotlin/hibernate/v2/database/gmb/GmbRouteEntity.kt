package hibernate.v2.database.gmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.database.BaseRouteEntity
import hibernate.v2.model.transport.TransportHashable
import hibernate.v2.model.transport.route.GmbTransportRoute
import hibernatev2database.Gmb_route

data class GmbRouteEntity(
    val routeId: String,
    val routeNo: String,
    val bound: Bound,
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
    val serviceType: String,
    val region: GmbRegion,
) : TransportHashable, BaseRouteEntity() {

    companion object {
        fun convertFrom(item: Gmb_route): GmbRouteEntity {
            return GmbRouteEntity(
                routeId = item.gmb_route_id,
                routeNo = item.gmb_route_no,
                bound = item.gmb_route_bound,
                region = item.region,
                origEn = item.orig_en,
                origTc = item.orig_tc,
                origSc = item.orig_sc,
                destEn = item.dest_en,
                destTc = item.dest_tc,
                destSc = item.dest_sc,
                serviceType = item.gmb_route_service_type
            )
        }
    }

    fun toTransportModel(): GmbTransportRoute {
        return GmbTransportRoute(
            routeId = routeId,
            routeNo = routeNo,
            bound = bound,
            origEn = origEn,
            origTc = origTc,
            origSc = origSc,
            destEn = destEn,
            destTc = destTc,
            destSc = destSc,
            company = Company.GMB,
            serviceType = serviceType,
            region = region
        )
    }

    fun routeHashId() = routeHashId(Company.GMB, routeId, bound, serviceType)
}
