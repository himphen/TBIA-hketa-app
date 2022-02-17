package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import hibernate.v2.sunshine.db.lrt.LRTRouteEntity
import hibernate.v2.sunshine.db.lrt.LRTStopEntity
import hibernate.v2.sunshine.model.Card

data class EtaLRTDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Embedded
    val route: LRTRouteEntity,
    @Embedded
    val stop: LRTStopEntity,
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
