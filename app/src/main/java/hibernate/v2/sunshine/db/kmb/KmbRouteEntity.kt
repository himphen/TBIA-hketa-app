package hibernate.v2.sunshine.db.kmb

import androidx.room.ColumnInfo
import androidx.room.Entity
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.sunshine.db.BaseRouteEntity
import hibernate.v2.sunshine.model.transport.TransportHashable
import hibernate.v2.sunshine.model.transport.TransportRoute

@Entity(
    tableName = "kmb_route",
    primaryKeys = ["kmb_route_id", "kmb_route_bound", "kmb_route_service_type"],
)
data class KmbRouteEntity(
    @ColumnInfo(name = "kmb_route_id")
    val routeId: String,
    @ColumnInfo(name = "kmb_route_bound")
    val bound: Bound,
    @ColumnInfo(name = "kmb_route_service_type")
    val serviceType: String,
    @ColumnInfo(name = "orig_en")
    val origEn: String,
    @ColumnInfo(name = "orig_tc")
    val origTc: String,
    @ColumnInfo(name = "orig_sc")
    val origSc: String,
    @ColumnInfo(name = "dest_en")
    val destEn: String,
    @ColumnInfo(name = "dest_tc")
    val destTc: String,
    @ColumnInfo(name = "dest_sc")
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
