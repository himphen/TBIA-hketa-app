package hibernate.v2.domain.lrt

import hibernate.v2.database.lrt.LrtDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val lrtDao: LrtDao
) {

    operator fun invoke() =
        lrtDao.getRouteStopComponentList()

    operator fun invoke(
        route: TransportRoute,
    ) = lrtDao.getRouteStopComponentList(
        route.routeId,
        route.bound,
        route.serviceType
    )
}
