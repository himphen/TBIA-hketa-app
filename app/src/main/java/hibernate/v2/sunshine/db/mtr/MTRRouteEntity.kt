package hibernate.v2.sunshine.db.mtr

import androidx.room.ColumnInfo
import androidx.room.Entity
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.mtr.MTRRoute
import hibernate.v2.sunshine.model.transport.TransportHashable
import hibernate.v2.sunshine.model.transport.TransportRoute

@Entity(
    tableName = "mtr_route",
    primaryKeys = ["mtr_route_id", "mtr_route_bound", "mtr_route_service_type"],
)
data class MTRRouteEntity(
    @ColumnInfo(name = "mtr_route_id")
    val routeId: String,
    @ColumnInfo(name = "mtr_route_bound")
    val bound: Bound,
    @ColumnInfo(name = "mtr_route_service_type")
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
    companion object {
        fun fromApiModel(route: MTRRoute): MTRRouteEntity {
            return MTRRouteEntity(
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
}