package hibernate.v2.domain.ctb

import hibernate.v2.database.ctb.CtbDao
import hibernate.v2.model.Card
import hibernate.v2.model.searchmap.SearchMapStop

class GetRouteEtaCardList(
    private val ctbDao: CtbDao
) {
    operator fun invoke(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = ctbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = ctbDao.getRouteListFromRouteId(routeStopList)
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
