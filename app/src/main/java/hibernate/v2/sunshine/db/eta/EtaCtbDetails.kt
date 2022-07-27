package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import hibernate.v2.sunshine.db.ctb.CtbRouteEntity
import hibernate.v2.sunshine.db.ctb.CtbStopEntity
import hibernate.v2.sunshine.model.Card

data class EtaCtbDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Embedded
    val route: CtbRouteEntity,
    @Embedded
    val stop: CtbStopEntity,
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
