package hibernate.v2.domain.nlb

import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.model.Card
import hibernate.v2.model.searchmap.SearchMapStop

class GetRouteEtaCardList(
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = nlbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = nlbDao.getRouteListFromRouteId(routeStopList)
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
