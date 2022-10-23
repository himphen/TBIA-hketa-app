package hibernate.v2.sunshine.domain.mtr

import hibernate.v2.sunshine.db.mtr.MtrDao
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop

class GetRouteEtaCardList(
    private val mtrDao: MtrDao
) {
    suspend operator fun invoke(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = mtrDao.getRouteStopListFromStopId(stop.stopId)
        val routeList = mtrDao.getRouteListFromRouteId(routeStopList)
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
