package hibernate.v2.sunshine.domain.ctb

import hibernate.v2.sunshine.db.ctb.CtbDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteListFromStopId(
    private val ctbDao: CtbDao
) {
    suspend operator fun invoke(stopId: String): List<TransportRoute> {
        val routeStopList = ctbDao.getRouteStopListFromStopId(stopId)
        val routeList = ctbDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }
}
