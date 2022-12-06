package hibernate.v2.database.mtr

import hibernatev2database.mtrDao.GetRouteStopComponentAllList
import hibernatev2database.mtrDao.GetRouteStopComponentList

data class MtrRouteStopComponent(
    val routeStopEntity: MtrRouteStopEntity,

    val stopEntity: MtrStopEntity?
) {
    companion object {
        fun convertFrom(item: GetRouteStopComponentAllList): MtrRouteStopComponent {
            val routeStopEntity = MtrRouteStopEntity(
                routeId = item.mtr_route_stop_route_id,
                bound = item.mtr_route_stop_bound,
                serviceType = item.mtr_route_stop_service_type,
                seq = item.mtr_route_stop_seq.toInt(),
                stopId = item.mtr_route_stop_stop_id
            )

            val stopEntity = MtrStopEntity(
                stopId = item.mtr_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return MtrRouteStopComponent(routeStopEntity, stopEntity)
        }

        fun convertFrom(item: GetRouteStopComponentList): MtrRouteStopComponent {
            val routeStopEntity = MtrRouteStopEntity(
                routeId = item.mtr_route_stop_route_id,
                bound = item.mtr_route_stop_bound,
                serviceType = item.mtr_route_stop_service_type,
                seq = item.mtr_route_stop_seq.toInt(),
                stopId = item.mtr_route_stop_stop_id
            )

            val stopEntity = MtrStopEntity(
                stopId = item.mtr_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return MtrRouteStopComponent(routeStopEntity, stopEntity)
        }
    }
}
