package hibernate.v2.domain.kmb

import hibernate.v2.database.kmb.KmbDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val kmbDao: KmbDao
) {

    operator fun invoke() =
        kmbDao.getRouteStopComponentList()

    operator fun invoke(
        route: TransportRoute,
    ) = kmbDao.getRouteStopComponentList(
        route.routeId,
        route.bound,
        route.serviceType
    )
}
