package hibernate.v2.database.mtr

import dev.icerock.moko.graphics.Color
import dev.icerock.moko.graphics.parseColor
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.BaseRouteEntity
import hibernate.v2.model.transport.TransportHashable
import hibernate.v2.model.transport.route.MTRRouteInfo
import hibernate.v2.model.transport.route.MtrTransportRoute
import hibernatev2database.Mtr_route

data class MtrRouteEntity(
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
    val routeInfoNameEn: String,
    val routeInfoNameTc: String,
    val routeInfoNameSc: String,
    val routeInfoColor: String,
    val routeInfoIsEnabled: Boolean,
) : TransportHashable, BaseRouteEntity() {
    companion object {
        fun convertFrom(route: Mtr_route): MtrRouteEntity {
            return MtrRouteEntity(
                routeId = route.mtr_route_id,
                bound = route.mtr_route_bound,
                serviceType = route.mtr_route_service_type,
                origEn = route.orig_en,
                origTc = route.orig_tc,
                origSc = route.orig_sc,
                destEn = route.dest_en,
                destTc = route.dest_tc,
                destSc = route.dest_sc,
                routeInfoNameEn = route.route_info_name_en,
                routeInfoNameTc = route.route_info_name_tc,
                routeInfoNameSc = route.route_info_name_sc,
                routeInfoColor = route.route_info_name_color,
                routeInfoIsEnabled = route.route_info_is_enabled,
            )
        }
    }

    fun toTransportModel(): MtrTransportRoute {
        return MtrTransportRoute(
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
}
