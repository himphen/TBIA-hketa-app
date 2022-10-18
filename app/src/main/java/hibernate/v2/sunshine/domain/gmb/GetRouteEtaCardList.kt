package hibernate.v2.sunshine.domain.gmb

import hibernate.v2.sunshine.db.gmb.GmbDao
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop

class GetRouteEtaCardList(
    private val gmbDao: GmbDao
) {
    suspend operator fun invoke(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = gmbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = gmbDao.getRouteListFromRouteId(routeStopList)
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
