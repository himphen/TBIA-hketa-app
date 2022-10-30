package hibernate.v2.sunshine.domain.lrt

import hibernate.v2.database.lrt.LrtDao
import hibernate.v2.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop

class GetRouteEtaCardList(
    private val lrtDao: LrtDao
) {
    suspend operator fun invoke(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = lrtDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = lrtDao.getRouteListFromRouteId(routeStopList)
        val routeHashMap = routeList.associateBy {
            it.routeHashId()
        }

        return routeStopList.mapNotNull {
            val route = routeHashMap[it.routeHashId()] ?: return@mapNotNull null

            Card.EtaCard(
                route.toTransportModel(),
                stop.toTransportModelWithSeq(it.seq),
                position = 0
            )
        }
    }
}
