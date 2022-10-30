package hibernate.v2.sunshine.domain.nlb

import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val nlbDao: NlbDao
) {

    suspend operator fun invoke() =
        nlbDao.getRouteStopComponentList()

    suspend operator fun invoke(
        route: TransportRoute,
    ) = nlbDao.getRouteStopComponentList(
        route.routeId
    )
}
