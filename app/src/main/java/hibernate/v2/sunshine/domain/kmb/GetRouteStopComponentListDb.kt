package hibernate.v2.sunshine.domain.kmb

import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val kmbDao: KmbDao
) {

    suspend operator fun invoke() =
        kmbDao.getRouteStopComponentList()

    suspend operator fun invoke(
        route: TransportRoute,
    ) = kmbDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value,
        route.serviceType
    )
}
