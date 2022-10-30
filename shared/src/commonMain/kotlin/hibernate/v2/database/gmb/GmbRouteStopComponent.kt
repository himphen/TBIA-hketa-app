package hibernate.v2.database.gmb

import hibernatev2database.gmbDao.GetRouteStopComponentAllList
import hibernatev2database.gmbDao.GetRouteStopComponentList

data class GmbRouteStopComponent(
    val routeStopEntity: GmbRouteStopEntity,

    val stopEntity: GmbStopEntity?
) {
    companion object {
        fun convertFrom(item: GetRouteStopComponentAllList): GmbRouteStopComponent {
            val routeStopEntity = GmbRouteStopEntity(
                routeId = item.gmb_route_stop_route_id,
                bound = item.gmb_route_stop_bound,
                serviceType = item.gmb_route_stop_service_type,
                seq = item.gmb_route_stop_seq.toInt(),
                stopId = item.gmb_route_stop_stop_id
            )

            val stopEntity = GmbStopEntity(
                stopId = item.gmb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return GmbRouteStopComponent(routeStopEntity, stopEntity)
        }

        fun convertFrom(item: GetRouteStopComponentList): GmbRouteStopComponent {
            val routeStopEntity = GmbRouteStopEntity(
                routeId = item.gmb_route_stop_route_id,
                bound = item.gmb_route_stop_bound,
                serviceType = item.gmb_route_stop_service_type,
                seq = item.gmb_route_stop_seq.toInt(),
                stopId = item.gmb_route_stop_stop_id
            )

            val stopEntity = GmbStopEntity(
                stopId = item.gmb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )

            return GmbRouteStopComponent(routeStopEntity, stopEntity)
        }
    }
}
