package hibernate.v2.domain.gmb

import hibernate.v2.database.gmb.GmbDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val gmbDao: GmbDao
) {

    operator fun invoke(routeIdList: List<String>) =
        gmbDao.getRouteStopComponentList(routeIdList)

    operator fun invoke(
        route: TransportRoute,
    ) = gmbDao.getRouteStopComponentList(
        route.routeId,
        route.bound,
        route.serviceType
    )
}
