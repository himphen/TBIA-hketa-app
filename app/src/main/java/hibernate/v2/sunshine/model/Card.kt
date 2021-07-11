package hibernate.v2.sunshine.model

import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop

sealed class Card {
    data class SettingsCard(
        var title: String,
        var description: String = "",
        var type: Type,
        var icon: Int,
    ) : Card() {
        enum class Type {
            ETA, TRAFFIC, NONE, VERSION
        }
    }

    data class SettingsEtaCard(
        val entity: SavedEtaEntity? = null,
        val route: TransportRoute? = null,
        val stop: TransportStop? = null,
        val position: Int = 0,
        val type: Type,
    ) : Card(), Comparable<SettingsEtaCard> {
        enum class Type {
            DATA, INSERT_ROW
        }

        override fun compareTo(other: SettingsEtaCard): Int {
            return position.compareTo(other.position)
        }
    }

    data class RouteStopAddCard(
        val route: TransportRoute,
        val stop: TransportStop,
    ) : Card()

    data class EtaCard(
        val route: TransportRoute,
        val stop: TransportStop,
        val position: Int,
        var etaList: List<TransportEta> = listOf(),
    ) : Card(), Comparable<EtaCard> {
        override fun compareTo(other: EtaCard): Int {
            return position.compareTo(other.position)
        }
    }
}