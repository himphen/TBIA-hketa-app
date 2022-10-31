package hibernate.v2.database.eta

import hibernate.v2.database.ctb.CtbRouteEntity
import hibernate.v2.database.ctb.CtbStopEntity
import hibernate.v2.model.Card
import hibernatev2database.GetAllCtbEtaWithOrdering

data class EtaCtbDetails(
    val savedEta: SavedEtaEntity,
    val route: CtbRouteEntity,
    val stop: CtbStopEntity,
    val order: SavedEtaOrderEntity,
) {
    companion object {
        fun convertFrom(item: GetAllCtbEtaWithOrdering): EtaCtbDetails {
            val savedEtaEntity = SavedEtaEntity(
                routeId = item.saved_eta_route_id,
                bound = item.saved_eta_route_bound,
                serviceType = item.saved_eta_service_type,
                seq = item.saved_eta_seq.toInt(),
                stopId = item.saved_eta_stop_id,
                id = item.saved_eta_id.toInt(),
                company = item.saved_eta_company
            )

            val routeEntity = CtbRouteEntity(
                routeId = item.ctb_route_id!!,
                bound = item.ctb_route_bound!!,
                company = item.ctb_route_company!!,
                origEn = item.orig_en!!,
                origTc = item.orig_tc!!,
                origSc = item.orig_sc!!,
                destEn = item.dest_en!!,
                destTc = item.dest_tc!!,
                destSc = item.dest_sc!!,
            )

            val stopEntity = CtbStopEntity(
                stopId = item.ctb_stop_id!!,
                nameEn = item.name_en!!,
                nameTc = item.name_tc!!,
                nameSc = item.name_sc!!,
                lat = item.lat!!,
                lng = item.lng!!,
                geohash = item.geohash!!,
            )

            return EtaCtbDetails(
                savedEta = savedEtaEntity,
                route = routeEntity,
                stop = stopEntity,
                order = SavedEtaOrderEntity(id = null, position = item.position.toInt())
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
