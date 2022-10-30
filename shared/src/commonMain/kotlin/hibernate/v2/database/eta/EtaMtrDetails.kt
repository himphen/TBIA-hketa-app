package hibernate.v2.database.eta

import hibernate.v2.database.mtr.MtrRouteEntity
import hibernate.v2.database.mtr.MtrStopEntity
import hibernate.v2.model.Card
import hibernatev2database.GetAllMtrEtaWithOrdering

data class EtaMtrDetails(
    val savedEta: SavedEtaEntity,
    val route: MtrRouteEntity,
    val stop: MtrStopEntity,
    val order: SavedEtaOrderEntity,
) {
    companion object {
        fun convertFrom(item: GetAllMtrEtaWithOrdering): EtaMtrDetails {
            val savedEtaEntity = SavedEtaEntity(
                routeId = item.saved_eta_route_id,
                bound = item.saved_eta_route_bound,
                serviceType = item.saved_eta_service_type,
                seq = item.saved_eta_seq.toInt(),
                stopId = item.saved_eta_stop_id,
                id = item.saved_eta_id,
                company = item.saved_eta_company
            )

            val routeEntity = MtrRouteEntity(
                routeId = item.mtr_route_id!!,
                bound = item.mtr_route_bound!!,
                origEn = item.orig_en!!,
                origTc = item.orig_tc!!,
                origSc = item.orig_sc!!,
                destEn = item.dest_en!!,
                destTc = item.dest_tc!!,
                destSc = item.dest_sc!!,
                serviceType = item.mtr_route_service_type!!,
                routeInfoNameEn = item.route_info_name_en!!,
                routeInfoNameTc = item.route_info_name_tc!!,
                routeInfoNameSc = item.route_info_name_sc!!,
                routeInfoColor = item.route_info_name_color!!,
                routeInfoIsEnabled = item.route_info_is_enabled!!,
            )

            val stopEntity = MtrStopEntity(
                stopId = item.mtr_stop_id!!,
                nameEn = item.name_en!!,
                nameTc = item.name_tc!!,
                nameSc = item.name_sc!!,
                lat = item.lat!!,
                lng = item.lng!!,
                geohash = item.geohash!!,
            )

            return EtaMtrDetails(
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
