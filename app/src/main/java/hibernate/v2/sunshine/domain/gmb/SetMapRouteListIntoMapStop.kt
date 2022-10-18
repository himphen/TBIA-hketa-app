package hibernate.v2.sunshine.domain.gmb

import hibernate.v2.sunshine.model.searchmap.SearchMapStop

class SetMapRouteListIntoMapStop {
    suspend operator fun invoke(
        stopList: List<SearchMapStop>,
        getRouteEtaCardList: GetRouteEtaCardList
    ): List<SearchMapStop> {
        return stopList.map {
            if (it.mapRouteList.isEmpty()) {
                it.mapRouteList = getRouteEtaCardList(it)
            }
            it
        }
    }
}
