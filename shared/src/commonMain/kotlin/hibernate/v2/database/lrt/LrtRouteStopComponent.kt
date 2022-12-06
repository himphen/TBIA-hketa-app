package hibernate.v2.database.lrt

import hibernatev2database.lrtDao.GetRouteStopComponentAllList
import hibernatev2database.lrtDao.GetRouteStopComponentList

data class LrtRouteStopComponent(
    val routeStopEntity: LrtRouteStopEntity,
    val stopEntity: LrtStopEntity?,
) {
    companion object {
        fun convertFrom(item: GetRouteStopComponentAllList): LrtRouteStopComponent {
            val routeStopEntity = LrtRouteStopEntity(
                routeId = item.lrt_route_stop_route_id,
                bound = item.lrt_route_stop_bound,
                serviceType = item.lrt_route_stop_service_type,
                seq = item.lrt_route_stop_seq.toInt(),
                stopId = item.lrt_route_stop_stop_id
            )

            val stopEntity = LrtStopEntity(
                stopId = item.lrt_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return LrtRouteStopComponent(routeStopEntity, stopEntity)
        }

        fun convertFrom(item: GetRouteStopComponentList): LrtRouteStopComponent {
            val routeStopEntity = LrtRouteStopEntity(
                routeId = item.lrt_route_stop_route_id,
                bound = item.lrt_route_stop_bound,
                serviceType = item.lrt_route_stop_service_type,
                seq = item.lrt_route_stop_seq.toInt(),
                stopId = item.lrt_route_stop_stop_id
            )

            val stopEntity = LrtStopEntity(
                stopId = item.lrt_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return LrtRouteStopComponent(routeStopEntity, stopEntity)
        }
    }
}
