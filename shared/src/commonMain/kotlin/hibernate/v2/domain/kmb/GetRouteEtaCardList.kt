package hibernate.v2.domain.kmb

import hibernate.v2.database.kmb.KmbDao
import hibernate.v2.model.Card
import hibernate.v2.model.searchmap.SearchMapStop

class GetRouteEtaCardList(
    private val kmbDao: KmbDao
) {
    suspend operator fun invoke(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = kmbDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = kmbDao.getRouteListFromRouteId(routeStopList)
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
