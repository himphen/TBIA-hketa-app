package hibernate.v2.sunshine.domain.nlb

import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.model.transport.route.TransportRoute

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
