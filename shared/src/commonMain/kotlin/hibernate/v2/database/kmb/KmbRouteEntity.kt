package hibernate.v2.database.kmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.BaseRouteEntity
import hibernate.v2.model.transport.TransportHashable
import hibernate.v2.model.transport.route.TransportRoute
import hibernatev2database.Kmb_route
import kotlinx.serialization.SerialName

data class KmbRouteEntity(
    @SerialName("kmb_route_id")
    val routeId: String,
    @SerialName("kmb_route_bound")
    val bound: Bound,
    @SerialName("kmb_route_service_type")
    val serviceType: String,
    @SerialName("orig_en")
    val origEn: String,
    @SerialName("orig_tc")
    val origTc: String,
    @SerialName("orig_sc")
    val origSc: String,
    @SerialName("dest_en")
    val destEn: String,
    @SerialName("dest_tc")
    val destTc: String,
    @SerialName("dest_sc")
    val destSc: String,
) : TransportHashable, BaseRouteEntity() {

    fun isSpecialRoute(): Boolean = serviceType != "1"

    companion object {
        fun convertFrom(item: Kmb_route): KmbRouteEntity {
            return KmbRouteEntity(
                routeId = item.kmb_route_id,
                bound = item.kmb_route_bound,
                serviceType = item.kmb_route_service_type,
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
            serviceType = serviceType,
            origEn = origEn,
            origTc = origTc,
            origSc = origSc,
            destEn = destEn,
            destTc = destTc,
            destSc = destSc,
            company = Company.KMB
        )
    }

    fun routeHashId() = routeHashId(Company.KMB, routeId, bound, serviceType)
}
