package hibernate.v2.database.eta

import hibernate.v2.database.gmb.GmbRouteEntity
import hibernate.v2.database.gmb.GmbStopEntity
import hibernate.v2.model.Card
import hibernatev2database.GetAllGmbEtaWithOrdering

data class EtaGmbDetails(
    val savedEta: SavedEtaEntity,
    val route: GmbRouteEntity,
    val stop: GmbStopEntity,
    val order: SavedEtaOrderEntity,
) {
    companion object {
        fun convertFrom(item: GetAllGmbEtaWithOrdering): EtaGmbDetails {
            val savedEtaEntity = SavedEtaEntity(
                routeId = item.saved_eta_route_id,
                bound = item.saved_eta_route_bound,
                serviceType = item.saved_eta_service_type,
                seq = item.saved_eta_seq.toInt(),
                stopId = item.saved_eta_stop_id,
                id = item.saved_eta_id,
                company = item.saved_eta_company
            )

            val routeEntity = GmbRouteEntity(
                routeId = item.gmb_route_id!!,
                bound = item.gmb_route_bound!!,
                region = item.region!!,
                origEn = item.orig_en!!,
                origTc = item.orig_tc!!,
                origSc = item.orig_sc!!,
                destEn = item.dest_en!!,
                destTc = item.dest_tc!!,
                destSc = item.dest_sc!!,
                routeNo = item.gmb_route_no!!,
                serviceType = item.gmb_route_service_type!!,
            )

            val stopEntity = GmbStopEntity(
                stopId = item.gmb_stop_id!!,
                nameEn = item.name_en!!,
                nameTc = item.name_tc!!,
                nameSc = item.name_sc!!,
                lat = item.lat!!,
                lng = item.lng!!,
                geohash = item.geohash!!,
            )

            return EtaGmbDetails(
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
