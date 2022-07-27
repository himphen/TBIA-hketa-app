package hibernate.v2.sunshine.db.ctb

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.ctb.CtbRoute
import hibernate.v2.sunshine.db.BaseRouteEntity
import hibernate.v2.sunshine.model.transport.TransportHashable
import hibernate.v2.sunshine.model.transport.TransportRoute

@Keep
@Entity(
    tableName = "ctb_route",
    primaryKeys = ["ctb_route_id", "ctb_route_bound"],
    indices = [Index("ctb_route_id", "ctb_route_bound")]
)
data class CtbRouteEntity(
    @ColumnInfo(name = "ctb_route_id")
    val routeId: String,
    @ColumnInfo(name = "ctb_route_bound")
    val bound: Bound,
    @ColumnInfo(name = "ctb_route_company")
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
) : TransportHashable, Comparable<CtbRouteEntity>, BaseRouteEntity() {
    @Ignore
    val serviceType = "1"

    companion object {
        fun fromApiModel(route: CtbRoute): CtbRouteEntity {
            return CtbRouteEntity(
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

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)

    override fun compareTo(other: CtbRouteEntity): Int {
        parseRouteNumber(routeId)
        other.parseRouteNumber(other.routeId)

        val companyCompare = company.compareTo(other.company)
        if (companyCompare != 0) return companyCompare

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        return bound.compareTo(other.bound)
    }
}
