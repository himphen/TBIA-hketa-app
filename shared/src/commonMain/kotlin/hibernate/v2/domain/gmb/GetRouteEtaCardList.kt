package hibernate.v2.domain.gmb

import hibernate.v2.database.gmb.GmbDao
import hibernate.v2.model.Card
import hibernate.v2.model.searchmap.SearchMapStop

class GetRouteEtaCardList(
    private val gmbDao: GmbDao
) {
    operator fun invoke(stop: SearchMapStop): List<Card.EtaCard> {
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
