package hibernate.v2.sunshine.domain.lrt

import hibernate.v2.sunshine.db.lrt.LrtDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val lrtDao: LrtDao
) {

    suspend operator fun invoke() =
        lrtDao.getRouteStopComponentList()

    suspend operator fun invoke(
        route: TransportRoute,
    ) = lrtDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value,
        route.serviceType
    )
}
