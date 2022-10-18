package hibernate.v2.sunshine.domain.gmb

import hibernate.v2.sunshine.db.gmb.GmbDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val gmbDao: GmbDao
) {

    suspend operator fun invoke(routeIdList: List<String>) =
        gmbDao.getRouteStopComponentList(routeIdList)

    suspend operator fun invoke(
        route: TransportRoute,
    ) = gmbDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value
    )
}
