package hibernate.v2.domain.gmb

import hibernate.v2.database.gmb.GmbDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteListFromStopId(
    private val gmbDao: GmbDao
) {
    operator fun invoke(stopId: String): List<TransportRoute> {
        val routeStopList = gmbDao.getRouteStopListFromStopId(stopId)
        val routeList = gmbDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }
}
