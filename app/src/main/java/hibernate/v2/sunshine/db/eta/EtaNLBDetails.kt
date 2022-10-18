package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import hibernate.v2.sunshine.db.nlb.NlbRouteEntity
import hibernate.v2.sunshine.db.nlb.NlbStopEntity
import hibernate.v2.sunshine.model.Card

data class EtaNlbDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Embedded
    val route: NlbRouteEntity,
    @Embedded
    val stop: NlbStopEntity,
    @Embedded
    val order: EtaOrderEntity,
) {
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
