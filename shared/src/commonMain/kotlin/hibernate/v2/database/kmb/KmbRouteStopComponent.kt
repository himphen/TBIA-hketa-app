package hibernate.v2.database.kmb

import hibernatev2database.kmbDao.GetRouteStopComponentAllList
import hibernatev2database.kmbDao.GetRouteStopComponentList

data class KmbRouteStopComponent(
    val routeStopEntity: KmbRouteStopEntity,
    val stopEntity: KmbStopEntity?
) {
    companion object {
        fun convertFrom(item: GetRouteStopComponentAllList): KmbRouteStopComponent {
            val routeStopEntity = KmbRouteStopEntity(
                routeId = item.kmb_route_stop_route_id,
                bound = item.kmb_route_stop_bound,
                serviceType = item.kmb_route_stop_service_type,
                seq = item.kmb_route_stop_seq.toInt(),
                stopId = item.kmb_route_stop_stop_id
            )

            val stopEntity = KmbStopEntity(
                stopId = item.kmb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return KmbRouteStopComponent(routeStopEntity, stopEntity)
        }

        fun convertFrom(item: GetRouteStopComponentList): KmbRouteStopComponent {
            val routeStopEntity = KmbRouteStopEntity(
                routeId = item.kmb_route_stop_route_id,
                bound = item.kmb_route_stop_bound,
                serviceType = item.kmb_route_stop_service_type,
                seq = item.kmb_route_stop_seq.toInt(),
                stopId = item.kmb_route_stop_stop_id
            )

            val stopEntity = KmbStopEntity(
                stopId = item.kmb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return KmbRouteStopComponent(routeStopEntity, stopEntity)
        }
    }
}