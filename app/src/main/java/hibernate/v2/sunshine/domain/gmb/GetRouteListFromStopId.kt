package hibernate.v2.sunshine.domain.gmb

import hibernate.v2.sunshine.db.gmb.GmbDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteListFromStopId(
    private val gmbDao: GmbDao
) {
    suspend operator fun invoke(stopId: String): List<TransportRoute> {
        val routeStopList = gmbDao.getRouteStopListFromStopId(stopId)
        val routeList = gmbDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }
}