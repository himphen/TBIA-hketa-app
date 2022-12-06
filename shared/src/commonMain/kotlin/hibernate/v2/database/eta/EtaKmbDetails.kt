package hibernate.v2.database.eta

import hibernate.v2.database.kmb.KmbRouteEntity
import hibernate.v2.database.kmb.KmbRouteStopEntity
import hibernate.v2.database.kmb.KmbStopEntity
import hibernate.v2.model.Card
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.route.TransportRoute
import hibernatev2database.GetAllKmbEtaWithOrdering

data class EtaKmbDetails(
    val savedEta: SavedEtaEntity,
    val route: KmbRouteEntity?,
    val routeStop: KmbRouteStopEntity?,
    val stop: KmbStopEntity?,
    val order: SavedEtaOrderEntity,
) {
    companion object {
        fun convertFrom(item: GetAllKmbEtaWithOrdering): EtaKmbDetails {
            val savedEtaEntity = SavedEtaEntity(
                routeId = item.saved_eta_route_id,
                bound = item.saved_eta_route_bound,
                serviceType = item.saved_eta_service_type,
                seq = item.saved_eta_seq.toInt(),
                stopId = item.saved_eta_stop_id,
                id = item.saved_eta_id.toInt(),
                company = item.saved_eta_company
            )

            val routeEntity = KmbRouteEntity(
                routeId = item.kmb_route_id!!,
                bound = item.kmb_route_bound!!,
                serviceType = item.kmb_route_service_type!!,
                origEn = item.orig_en!!,
                origTc = item.orig_tc!!,
                origSc = item.orig_sc!!,
                destEn = item.dest_en!!,
                destTc = item.dest_tc!!,
                destSc = item.dest_sc!!,
            )

            val routeStopEntity = KmbRouteStopEntity(
                routeId = item.kmb_route_stop_route_id!!,
                bound = item.kmb_route_stop_bound!!,
                serviceType = item.kmb_route_stop_service_type!!,
                seq = item.kmb_route_stop_seq!!.toInt(),
                stopId = item.kmb_route_stop_stop_id!!
            )

            val stopEntity = KmbStopEntity(
                stopId = item.kmb_stop_id!!,
                nameEn = item.name_en!!,
                nameTc = item.name_tc!!,
                nameSc = item.name_sc!!,
                lat = item.lat!!,
                lng = item.lng!!,
                geohash = item.geohash!!,
            )

            return EtaKmbDetails(
                savedEta = savedEtaEntity,
                route = routeEntity,
                routeStop = routeStopEntity,
                stop = stopEntity,
                order = SavedEtaOrderEntity(id = null, position = item.position.toInt())
            )
        }
    }

    fun toSettingsEtaCard(): Card.SettingsEtaItemCard {
        return Card.SettingsEtaItemCard(
            entity = savedEta,
            route = route?.toTransportModel() ?: TransportRoute.notFoundRoute(),
            stop = stop?.toTransportModel() ?: TransportStop.notFoundStop(),
            position = order.position
        )
    }

    fun toEtaCard(): Card.EtaCard {
        return Card.EtaCard(
            route = route?.toTransportModel() ?: TransportRoute.notFoundRoute(),
            stop = stop?.toTransportModelWithSeq(savedEta.seq) ?: TransportStop.notFoundStop(),
            position = order.position,
            isValid = route != null && stop != null && routeStop != null
        )
    }
}
