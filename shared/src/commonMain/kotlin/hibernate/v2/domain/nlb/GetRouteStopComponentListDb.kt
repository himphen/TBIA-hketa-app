package hibernate.v2.domain.nlb

import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val nlbDao: NlbDao
) {

    operator fun invoke() =
        nlbDao.getRouteStopComponentList()

    operator fun invoke(
        route: TransportRoute,
    ) = nlbDao.getRouteStopComponentList(
        route.routeId
    )
}
