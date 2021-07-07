package hibernate.v2.sunshine.db.kmb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.transport.KmbRoute
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.model.transport.RouteHashable
import hibernate.v2.sunshine.model.transport.TransportRoute

@Entity(
    tableName = "kmb_route",
    primaryKeys = ["route", "bound", "service_type"],
    indices = [Index("route", "bound", "service_type")]
)
data class KmbRouteEntity(
    @ColumnInfo(name = "route")
    val routeId: String,
    val bound: Bound,
    @ColumnInfo(name = "service_type")
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
) : RouteHashable {
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

    override fun routeHashId() = Company.KMB.value + routeId + bound.value + serviceType
}