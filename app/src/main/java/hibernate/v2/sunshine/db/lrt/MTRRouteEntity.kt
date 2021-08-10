package hibernate.v2.sunshine.db.lrt

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.lrt.LRTRoute
import hibernate.v2.sunshine.model.transport.LRTRouteInfo
import hibernate.v2.sunshine.model.transport.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.TransportHashable

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
) : TransportHashable {
    companion object {
        fun fromApiModel(route: LRTRoute): LRTRouteEntity {
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
}