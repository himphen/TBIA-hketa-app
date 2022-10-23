package hibernate.v2.sunshine.domain.mtr

import hibernate.v2.sunshine.db.mtr.MtrDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteListFromStopId(
    private val mtrDao: MtrDao
) {
    suspend operator fun invoke(stopId: String): List<TransportRoute> {
        val routeStopList = mtrDao.getRouteStopListFromStopId(stopId)
        val routeList = mtrDao.getRouteListFromRouteId(routeStopList)

        return routeList.map {
            it.toTransportModel()
        }
    }
}
