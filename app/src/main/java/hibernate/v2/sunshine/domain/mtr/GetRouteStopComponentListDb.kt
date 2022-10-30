package hibernate.v2.sunshine.domain.mtr

import hibernate.v2.database.mtr.MtrDao
import hibernate.v2.model.transport.route.TransportRoute

class GetRouteStopComponentListDb(
    private val mtrDao: MtrDao
) {

    suspend operator fun invoke() =
        mtrDao.getRouteStopComponentList()

    suspend operator fun invoke(
        route: TransportRoute,
    ) = mtrDao.getRouteStopComponentList(
        route.routeId,
        route.bound,
        route.serviceType
    )
}
