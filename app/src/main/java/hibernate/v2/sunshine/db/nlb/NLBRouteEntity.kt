package hibernate.v2.sunshine.db.nlb

import androidx.room.ColumnInfo
import androidx.room.Entity
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.nlb.NLBRoute
import hibernate.v2.sunshine.model.transport.TransportHashable
import hibernate.v2.sunshine.model.transport.TransportRoute

@Entity(
    tableName = "nlb_route",
    primaryKeys = ["nlb_route_id", "nlb_route_bound", "nlb_route_service_type"],
)
data class NLBRouteEntity(
    @ColumnInfo(name = "nlb_route_id")
    val routeId: String,
    @ColumnInfo(name = "nlb_route_bound")
    val bound: Bound,
    @ColumnInfo(name = "nlb_route_service_type")
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
) : TransportHashable {

    fun isSpecialRoute(): Boolean = false

    companion object {
        fun fromApiModel(route: NLBRoute): NLBRouteEntity {
            return NLBRouteEntity(
                routeId = route.routeId.toString(),
                bound = route.bound,
                serviceType = route.serviceType.toString(),
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
}