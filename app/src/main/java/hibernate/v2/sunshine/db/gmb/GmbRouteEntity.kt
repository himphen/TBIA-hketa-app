package hibernate.v2.sunshine.db.gmb

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.api.model.transport.gmb.GmbRoute
import hibernate.v2.sunshine.db.BaseRouteEntity
import hibernate.v2.sunshine.model.transport.route.GmbTransportRoute
import hibernate.v2.sunshine.model.transport.TransportHashable

@Keep
@Entity(
    tableName = "gmb_route",
    primaryKeys = ["gmb_route_id", "gmb_route_bound", "gmb_route_service_type"]
)
data class GmbRouteEntity(
    @ColumnInfo(name = "gmb_route_id")
    val routeId: String,
    @ColumnInfo(name = "gmb_route_no")
    val routeNo: String,
    @ColumnInfo(name = "gmb_route_bound")
    val bound: Bound,
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
    @ColumnInfo(name = "gmb_route_service_type")
    val serviceType: String,
    @ColumnInfo(name = "region")
    val region: GmbRegion,
) : TransportHashable, Comparable<GmbRouteEntity>, BaseRouteEntity() {

    companion object {
        fun fromApiModel(route: GmbRoute): GmbRouteEntity {
            return GmbRouteEntity(
                routeId = route.routeId.toString(),
                routeNo = route.routeNo,
                bound = route.bound,
                region = route.region,
                origEn = route.origEn,
                origTc = route.origTc,
                origSc = route.origSc,
                destEn = route.destEn,
                destTc = route.destTc,
                destSc = route.destSc,
                serviceType = route.serviceType.toString()
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

    override fun compareTo(other: GmbRouteEntity): Int {
        parseRouteNumber(routeNo)
        other.parseRouteNumber(routeNo)

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }
}
