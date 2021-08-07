package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import hibernate.v2.sunshine.db.mtr.MTRRouteEntity
import hibernate.v2.sunshine.db.mtr.MTRStopEntity
import hibernate.v2.sunshine.model.Card
data class EtaMTRDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Embedded
    val route: MTRRouteEntity,
    @Embedded
    val stop: MTRStopEntity,
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