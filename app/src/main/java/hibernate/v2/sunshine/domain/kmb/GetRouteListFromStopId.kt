package hibernate.v2.sunshine.domain.kmb

import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteListFromStopId(
    private val kmbDao: KmbDao
) {
    suspend operator fun invoke(stopId: String): List<TransportRoute> {
        val routeStopList = kmbDao.getRouteStopListFromStopId(stopId)
        val routeList = kmbDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }
}
