package hibernate.v2.sunshine.db.nlb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.nlb.NLBRoute
import hibernate.v2.sunshine.db.BaseRouteEntity
import hibernate.v2.sunshine.model.transport.TransportHashable
import hibernate.v2.sunshine.model.transport.TransportRoute

@Entity(
    tableName = "nlb_route",
    primaryKeys = ["nlb_route_id"],
)
data class NLBRouteEntity(
    @ColumnInfo(name = "nlb_route_id")
    val routeId: String,
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
) : TransportHashable, Comparable<NLBRouteEntity>, BaseRouteEntity() {
    @Ignore
    val bound = Bound.UNKNOWN

    @Ignore
    val serviceType = "1"

    fun isSpecialRoute(): Boolean = false

    companion object {
        fun fromApiModel(route: NLBRoute): NLBRouteEntity {
            return NLBRouteEntity(
                routeId = route.routeId,
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
            company = Company.NLB
        )
    }

    fun routeHashId() = routeHashId(Company.NLB, routeId, bound, serviceType)

    override fun compareTo(other: NLBRouteEntity): Int {
        parseRouteNumber(routeId)
        other.parseRouteNumber(other.routeId)

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }
}
