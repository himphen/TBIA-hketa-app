package hibernate.v2.sunshine.db.lrt

import android.graphics.Color
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.lrt.LrtRoute
import hibernate.v2.sunshine.db.BaseRouteEntity
import hibernate.v2.sunshine.model.transport.route.LRTRouteInfo
import hibernate.v2.sunshine.model.transport.route.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.TransportHashable

@Keep
@Entity(
    tableName = "lrt_route",
    primaryKeys = ["lrt_route_id", "lrt_route_bound", "lrt_route_service_type"],
)
data class LRTRouteEntity(
    @ColumnInfo(name = "lrt_route_id")
    val routeId: String,
    @ColumnInfo(name = "lrt_route_bound")
    val bound: Bound,
    @ColumnInfo(name = "lrt_route_service_type")
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
    @ColumnInfo(name = "route_info_name_color")
    val routeInfoColor: String,
    @ColumnInfo(name = "route_info_is_enabled")
    val routeInfoIsEnabled: Boolean,
) : TransportHashable, Comparable<LRTRouteEntity>, BaseRouteEntity() {
    companion object {
        fun fromApiModel(route: LrtRoute): LRTRouteEntity {
            return LRTRouteEntity(
                routeId = route.routeId,
                bound = route.bound,
                serviceType = route.serviceType,
                origEn = route.origEn,
                origTc = route.origTc,
                origSc = route.origSc,
                destEn = route.destEn,
                destTc = route.destTc,
                destSc = route.destSc,
                routeInfoColor = route.routeInfo.color,
                routeInfoIsEnabled = route.routeInfo.isEnabled,
            )
        }
    }

    fun toTransportModel(): LRTTransportRoute {
        return LRTTransportRoute(
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
            company = Company.LRT,
            routeInfo = LRTRouteInfo(
                color = try {
                    Color.parseColor(routeInfoColor)
                } catch (e: IllegalArgumentException) {
                    Color.parseColor("#2979FF")
                }
            )
        )
    }

    fun routeHashId() = routeHashId(Company.LRT, routeId, bound, serviceType)

    override fun compareTo(other: LRTRouteEntity): Int {
        parseRouteNumber(routeId)
        other.parseRouteNumber(other.routeId)

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }
}
