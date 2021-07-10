package hibernate.v2.sunshine.db.nc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.nc.NCRoute
import hibernate.v2.sunshine.model.transport.TransportRoute

@Entity(
    tableName = "nc_route",
    primaryKeys = ["nc_route_id", "nc_route_bound"],
    indices = [Index("nc_route_id", "nc_route_bound")]
)
data class NCRouteEntity(
    @ColumnInfo(name = "nc_route_id")
    val routeId: String,
    @ColumnInfo(name = "nc_route_bound")
    val bound: Bound,
    @ColumnInfo(name = "nc_route_company")
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
) {
    @Ignore
    val serviceType = "1"

    companion object {
        fun fromApiModel(route: NCRoute): NCRouteEntity {
            return NCRouteEntity(
                routeId = route.routeId,
                bound = route.bound,
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
            routeNo = routeId,
            bound = bound,
            origEn = origEn,
            origTc = origTc,
            origSc = origSc,
            destEn = destEn,
            destTc = destTc,
            destSc = destSc,
            company = company,
            serviceType = "1"
        )
    }
}