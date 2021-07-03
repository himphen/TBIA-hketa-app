package hibernate.v2.sunshine.db.kmb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.Bound
import hibernate.v2.api.model.Route
import hibernate.v2.sunshine.db.eta.Brand
import hibernate.v2.sunshine.model.RouteHashable
import hibernate.v2.sunshine.model.TransportRoute

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
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
) : RouteHashable {
    companion object {
        fun fromApiModel(route: Route): KmbRouteEntity {
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
            brand = Brand.KMB
        )
    }

    override fun routeHashId() = routeId + bound.value + serviceType
}