package hibernate.v2.sunshine.domain.mtr

import hibernate.v2.sunshine.db.mtr.MtrDao
import hibernate.v2.sunshine.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val mtrDao: MtrDao
) {

    suspend operator fun invoke() =
        mtrDao.getRouteStopComponentList()

    suspend operator fun invoke(
        route: TransportRoute,
    ) = mtrDao.getRouteStopComponentList(
        route.routeId,
        route.bound.value,
        route.serviceType
    )
}
