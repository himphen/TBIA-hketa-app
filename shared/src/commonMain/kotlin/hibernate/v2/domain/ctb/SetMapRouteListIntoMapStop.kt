package hibernate.v2.domain.ctb

import hibernate.v2.model.searchmap.SearchMapStop

class SetMapRouteListIntoMapStop {
    operator fun invoke(
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
