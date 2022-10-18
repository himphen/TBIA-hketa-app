package hibernate.v2.sunshine.domain.nlb

import hibernate.v2.sunshine.db.nlb.NlbDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteListFromStopId(
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke(stopId: String): List<TransportRoute> {
        val routeStopList = nlbDao.getRouteStopListFromStopId(stopId)
        val routeList = nlbDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }
}
