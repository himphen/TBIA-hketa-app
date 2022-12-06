package hibernate.v2.database.nlb

import hibernatev2database.nlbDao.GetRouteStopComponentAllList
import hibernatev2database.nlbDao.GetRouteStopComponentList

data class NlbRouteStopComponent(
    val routeStopEntity: NlbRouteStopEntity,
    val stopEntity: NlbStopEntity?
) {
    companion object {
        fun convertFrom(item: GetRouteStopComponentAllList): NlbRouteStopComponent {
            val routeStopEntity = NlbRouteStopEntity(
                routeId = item.nlb_route_stop_route_id,
                seq = item.nlb_route_stop_seq.toInt(),
                stopId = item.nlb_route_stop_stop_id
            )

            val stopEntity = NlbStopEntity(
                stopId = item.nlb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return NlbRouteStopComponent(routeStopEntity, stopEntity)
        }

        fun convertFrom(item: GetRouteStopComponentList): NlbRouteStopComponent {
            val routeStopEntity = NlbRouteStopEntity(
                routeId = item.nlb_route_stop_route_id,
                seq = item.nlb_route_stop_seq.toInt(),
                stopId = item.nlb_route_stop_stop_id
            )

            val stopEntity = NlbStopEntity(
                stopId = item.nlb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return NlbRouteStopComponent(routeStopEntity, stopEntity)
        }
    }
}
