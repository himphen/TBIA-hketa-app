package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop

data class EtaKmbDetails(
    @Embedded
    val savedEta: SavedEtaEntity,
    @Embedded
    val route: KmbRouteEntity?,
    @Embedded
    val routeStop: KmbRouteStopEntity?,
    @Embedded
    val stop: KmbStopEntity?,
    @Embedded
    val order: EtaOrderEntity,
) {
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