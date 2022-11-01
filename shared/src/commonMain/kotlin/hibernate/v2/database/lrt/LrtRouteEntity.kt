package hibernate.v2.database.lrt

import dev.icerock.moko.graphics.Color
import dev.icerock.moko.graphics.parseColor
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.BaseRouteEntity
import hibernate.v2.model.transport.TransportHashable
import hibernate.v2.model.transport.route.LrtRouteInfo
import hibernate.v2.model.transport.route.LrtTransportRoute
import hibernatev2database.Lrt_route

data class LrtRouteEntity(
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
    val routeInfoColor: String,
    val routeInfoIsEnabled: Boolean,
) : TransportHashable, BaseRouteEntity() {
    companion object {
        fun convertFrom(item: Lrt_route): LrtRouteEntity {
            return LrtRouteEntity(
                routeId = item.lrt_route_id,
                bound = item.lrt_route_bound,
                serviceType = item.lrt_route_service_type,
                origEn = item.orig_en,
                origTc = item.orig_tc,
                origSc = item.orig_sc,
                destEn = item.dest_en,
                destTc = item.dest_tc,
                destSc = item.dest_sc,
                routeInfoColor = item.route_info_name_color,
                routeInfoIsEnabled = item.route_info_is_enabled,
            )
        }
    }

    fun toTransportModel(): LrtTransportRoute {
        return LrtTransportRoute(
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
            routeInfo = LrtRouteInfo(
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
