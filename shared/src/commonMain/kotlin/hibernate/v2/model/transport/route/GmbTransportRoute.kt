package hibernate.v2.model.transport.route

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.GmbRegion

data class GmbTransportRoute(
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
    val region: GmbRegion
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

    override fun compareTo(other: TransportRoute): Int {
        if (other is GmbTransportRoute) {
            val result = region.ordering.compareTo(other.region.ordering)
            if (result != 0) {
                return result
            }
        }

        return super.compareTo(other)
    }

    override fun isSpecialRoute(): Boolean = false
}
