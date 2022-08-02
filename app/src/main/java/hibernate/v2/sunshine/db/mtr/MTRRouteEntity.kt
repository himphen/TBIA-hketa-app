package hibernate.v2.sunshine.db.mtr

import android.graphics.Color
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.mtr.MtrRoute
import hibernate.v2.sunshine.db.BaseRouteEntity
import hibernate.v2.sunshine.model.transport.route.MTRRouteInfo
import hibernate.v2.sunshine.model.transport.route.MTRTransportRoute
import hibernate.v2.sunshine.model.transport.TransportHashable

@Keep
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
    @ColumnInfo(name = "route_info_name_en")
    val routeInfoNameEn: String,
    @ColumnInfo(name = "route_info_name_tc")
    val routeInfoNameTc: String,
    @ColumnInfo(name = "route_info_name_sc")
    val routeInfoNameSc: String,
    @ColumnInfo(name = "route_info_name_color")
    val routeInfoColor: String,
    @ColumnInfo(name = "route_info_is_enabled")
    val routeInfoIsEnabled: Boolean,
) : TransportHashable, Comparable<MTRRouteEntity>, BaseRouteEntity() {
    companion object {
        fun fromApiModel(route: MtrRoute): MTRRouteEntity {
            return MTRRouteEntity(
                routeId = route.routeId,
                bound = route.bound,
                serviceType = route.serviceType,
                origEn = route.origEn,
                origTc = route.origTc,
                origSc = route.origSc,
                destEn = route.destEn,
                destTc = route.destTc,
                destSc = route.destSc,
                routeInfoNameEn = route.routeInfo.nameEn,
                routeInfoNameTc = route.routeInfo.nameTc,
                routeInfoNameSc = route.routeInfo.nameTc,
                routeInfoColor = route.routeInfo.color,
                routeInfoIsEnabled = route.routeInfo.isEnabled,
            )
        }
    }

    fun toTransportModel(): MTRTransportRoute {
        return MTRTransportRoute(
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
            company = Company.MTR,
            routeInfo = MTRRouteInfo(
                nameEn = routeInfoNameEn,
                nameTc = routeInfoNameTc,
                color = try {
                    Color.parseColor(routeInfoColor)
                } catch (e: IllegalArgumentException) {
                    Color.parseColor("#2979FF")
                }
            )
        )
    }

    fun routeHashId() = routeHashId(Company.MTR, routeId, bound, serviceType)

    override fun compareTo(other: MTRRouteEntity): Int {
        val routeIdCompare = routeId.compareTo(other.routeId)
        if (routeIdCompare != 0) return routeIdCompare

        val boundCompare = bound.compareTo(other.bound)
        if (boundCompare != 0) return boundCompare

        return serviceType.compareTo(other.serviceType)
    }
}
