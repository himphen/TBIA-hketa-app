package hibernate.v2.sunshine.domain.ctb

import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.ctb.CtbDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val ctbDao: CtbDao
) {

    operator fun invoke(company: Company) =
        ctbDao.getRouteStopComponentList(company)

    operator fun invoke(
        route: TransportRoute,
    ) = ctbDao.getRouteStopComponentList(
        route.company,
        route.routeId,
        route.bound
    )
}
