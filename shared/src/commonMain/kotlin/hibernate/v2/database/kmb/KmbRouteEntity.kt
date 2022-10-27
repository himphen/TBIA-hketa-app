package hibernate.v2.database.kmb

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.database.BaseRouteEntity
import hibernate.v2.model.transport.TransportHashable
import hibernate.v2.sunshine.model.transport.route.TransportRoute
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
) : TransportHashable, Comparable<KmbRouteEntity>, BaseRouteEntity() {

    fun isSpecialRoute(): Boolean = serviceType != "1"

    companion object {
        fun fromApiModel(route: KmbRoute): KmbRouteEntity {
            return KmbRouteEntity(
                routeId = route.routeId,
                bound = route.bound,
                serviceType = route.serviceType,
                origEn = route.origEn,
                origTc = route.origTc,
                origSc = route.origSc,
                destEn = route.destEn,
                destTc = route.destTc,
                destSc = route.destSc
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

    override fun compareTo(other: KmbRouteEntity): Int {
        parseRouteNumber(routeId)
        other.parseRouteNumber(other.routeId)

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }
}
