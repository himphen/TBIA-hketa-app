package hibernate.v2.sunshine.domain.lrt

import hibernate.v2.database.lrt.LrtDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteListFromStopId(
    private val lrtDao: LrtDao
) {
    suspend operator fun invoke(stopId: String): List<TransportRoute> {
        val routeStopList = lrtDao.getRouteStopListFromStopId(stopId)
        val routeList = lrtDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }
}
