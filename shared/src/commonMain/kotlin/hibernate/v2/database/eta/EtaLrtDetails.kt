package hibernate.v2.database.eta

import hibernate.v2.database.lrt.LrtRouteEntity
import hibernate.v2.database.lrt.LrtStopEntity
import hibernate.v2.model.Card
import hibernatev2database.GetAllLrtEtaWithOrdering

data class EtaLrtDetails(
    val savedEta: SavedEtaEntity,
    val route: LrtRouteEntity,
    val stop: LrtStopEntity,
    val order: SavedEtaOrderEntity,
) {
    companion object {
        fun convertFrom(item: GetAllLrtEtaWithOrdering): EtaLrtDetails {
            val savedEtaEntity = SavedEtaEntity(
                routeId = item.saved_eta_route_id,
                bound = item.saved_eta_route_bound,
                serviceType = item.saved_eta_service_type,
                seq = item.saved_eta_seq.toInt(),
                stopId = item.saved_eta_stop_id,
                id = item.saved_eta_id.toInt(),
                company = item.saved_eta_company
            )

            val routeEntity = LrtRouteEntity(
                routeId = item.lrt_route_id!!,
                bound = item.lrt_route_bound!!,
                origEn = item.orig_en!!,
                origTc = item.orig_tc!!,
                origSc = item.orig_sc!!,
                destEn = item.dest_en!!,
                destTc = item.dest_tc!!,
                destSc = item.dest_sc!!,
                serviceType = item.lrt_route_service_type!!,
                routeInfoColor = item.route_info_name_color!!,
                routeInfoIsEnabled = item.route_info_is_enabled!!,
            )

            val stopEntity = LrtStopEntity(
                stopId = item.lrt_stop_id!!,
                nameEn = item.name_en!!,
                nameTc = item.name_tc!!,
                nameSc = item.name_sc!!,
                lat = item.lat!!,
                lng = item.lng!!,
                geohash = item.geohash!!,
            )

            return EtaLrtDetails(
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
