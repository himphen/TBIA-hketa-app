package hibernate.v2.database.ctb

import hibernatev2database.ctbDao.GetRouteStopComponentAllList
import hibernatev2database.ctbDao.GetRouteStopComponentList

data class CtbRouteStopComponent(
    val routeStopEntity: CtbRouteStopEntity,

    val stopEntity: CtbStopEntity?
) {

    companion object {

        fun convertFrom(item: GetRouteStopComponentAllList): CtbRouteStopComponent {
            val routeStopEntity = CtbRouteStopEntity(
                routeId = item.ctb_route_stop_route_id,
                bound = item.ctb_route_stop_bound,
                company = item.ctb_route_stop_company,
                seq = item.ctb_route_stop_seq.toInt(),
                stopId = item.ctb_route_stop_stop_id
            )

            val stopEntity = CtbStopEntity(
                stopId = item.ctb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return CtbRouteStopComponent(routeStopEntity, stopEntity)
        }

        fun convertFrom(item: GetRouteStopComponentList): CtbRouteStopComponent {
            val routeStopEntity = CtbRouteStopEntity(
                routeId = item.ctb_route_stop_route_id,
                bound = item.ctb_route_stop_bound,
                company = item.ctb_route_stop_company,
                seq = item.ctb_route_stop_seq.toInt(),
                stopId = item.ctb_route_stop_stop_id
            )

            val stopEntity = CtbStopEntity(
                stopId = item.ctb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return CtbRouteStopComponent(routeStopEntity, stopEntity)
        }
    }
}