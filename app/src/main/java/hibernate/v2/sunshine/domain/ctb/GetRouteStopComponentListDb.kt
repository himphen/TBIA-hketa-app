package hibernate.v2.sunshine.domain.ctb

import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.db.ctb.CtbDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val ctbDao: CtbDao
) {

    suspend operator fun invoke(company: Company) =
        ctbDao.getRouteStopComponentList(company.value)

    suspend operator fun invoke(
        route: TransportRoute,
    ) = ctbDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value,
        route.serviceType
    )
}
