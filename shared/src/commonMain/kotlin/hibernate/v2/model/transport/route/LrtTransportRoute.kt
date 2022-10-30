package hibernate.v2.model.transport.route

import dev.icerock.moko.graphics.Color
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company

data class LrtTransportRoute(
    override val routeId: String,
    override val routeNo: String,
    override val bound: Bound,
    override val serviceType: String,
    override val origEn: String,
    override val origTc: String,
    override val origSc: String,
    override val destEn: String,
    override val destTc: String,
    override val destSc: String,
    override val company: Company,
    val routeInfo: LrtRouteInfo,
) : TransportRoute(
    routeId,
    routeNo,
    bound,
    serviceType,
    origEn,
    origTc,
    origSc,
    destEn,
    destTc,
    destSc,
    company,
) {

    override fun isSpecialRoute(): Boolean = false
}

data class LrtRouteInfo(
    var color: Color,
)
