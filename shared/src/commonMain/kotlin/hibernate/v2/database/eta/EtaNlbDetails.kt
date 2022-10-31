package hibernate.v2.database.eta

import hibernate.v2.database.nlb.NlbRouteEntity
import hibernate.v2.database.nlb.NlbStopEntity
import hibernate.v2.model.Card
import hibernatev2database.GetAllNlbEtaWithOrdering

data class EtaNlbDetails(
    val savedEta: SavedEtaEntity,
    val route: NlbRouteEntity,
    val stop: NlbStopEntity,
    val order: SavedEtaOrderEntity,
) {
    companion object {
        fun convertFrom(item: GetAllNlbEtaWithOrdering): EtaNlbDetails {
            val savedEtaEntity = SavedEtaEntity(
                routeId = item.saved_eta_route_id,
                bound = item.saved_eta_route_bound,
                serviceType = item.saved_eta_service_type,
                seq = item.saved_eta_seq.toInt(),
                stopId = item.saved_eta_stop_id,
                id = item.saved_eta_id.toInt(),
                company = item.saved_eta_company
            )

            val routeEntity = NlbRouteEntity(
                routeId = item.nlb_route_id!!,
                origEn = item.orig_en!!,
                origTc = item.orig_tc!!,
                origSc = item.orig_sc!!,
                destEn = item.dest_en!!,
                destTc = item.dest_tc!!,
                destSc = item.dest_sc!!,
            )

            val stopEntity = NlbStopEntity(
                stopId = item.nlb_stop_id!!,
                nameEn = item.name_en!!,
                nameTc = item.name_tc!!,
                nameSc = item.name_sc!!,
                lat = item.lat!!,
                lng = item.lng!!,
                geohash = item.geohash!!,
            )

            return EtaNlbDetails(
                savedEta = savedEtaEntity,
                route = routeEntity,
                stop = stopEntity,
                order = SavedEtaOrderEntity(id = null, position = 0)
            )
        }
    }

    fun toSettingsEtaCard(): Card.SettingsEtaItemCard {
        return Card.SettingsEtaItemCard(
            entity = savedEta,
            route = route.toTransportModel(),
            stop = stop.toTransportModel(),
            position = order.position
        )
    }

    fun toEtaCard(): Card.EtaCard {
        return Card.EtaCard(
            route = route.toTransportModel(),
            stop = stop.toTransportModelWithSeq(savedEta.seq),
            position = order.position
        )
    }
}
