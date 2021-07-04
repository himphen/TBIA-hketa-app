package hibernate.v2.sunshine.db.nc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.api.model.nc.Company
import hibernate.v2.api.model.nc.NCRoute
import hibernate.v2.sunshine.db.eta.Brand
import hibernate.v2.sunshine.model.transport.RouteHashable
import hibernate.v2.sunshine.model.transport.TransportRoute

@Entity(
    tableName = "nc_route",
    primaryKeys = ["route", "bound"],
    indices = [Index("route", "bound")]
)
data class NCRouteEntity(
    @ColumnInfo(name = "route")
    val routeId: String,
    val bound: Bound,
    val company: Company,
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
        fun fromApiModel(route: NCRoute, bound: Bound): NCRouteEntity {
            return NCRouteEntity(
                routeId = route.routeId,
                bound = bound,
                company = route.company,
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
            origEn = origEn,
            origTc = origTc,
            origSc = origSc,
            destEn = destEn,
            destTc = destTc,
            destSc = destSc,
            brand = Brand.KMB
        )
    }

    override fun routeHashId() = routeId + bound.value
}