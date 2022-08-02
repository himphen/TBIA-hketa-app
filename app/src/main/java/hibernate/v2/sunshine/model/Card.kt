package hibernate.v2.sunshine.model

import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.transport.eta.TransportEta
import hibernate.v2.sunshine.model.transport.route.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop

sealed class Card {
    data class SettingsCard(
        var title: String,
        var description: String = "",
        var type: Type,
        var icon: Int,
    ) : Card() {
        enum class Type {
            ETA, ETA_LAYOUT, TRAFFIC, NONE, VERSION, LANG
        }
    }

    abstract class SettingsEtaCard(
        open val position: Int = 0,
    ) : Card(), Comparable<SettingsEtaCard> {
        override fun compareTo(other: SettingsEtaCard): Int {
            return position.compareTo(other.position)
        }
    }

    data class SettingsEtaItemCard(
        val entity: SavedEtaEntity,
        val route: TransportRoute,
        val stop: TransportStop,
        override val position: Int,
        val isValid: Boolean = true,
    ) : SettingsEtaCard()

    class SettingsEtaAddCard : SettingsEtaCard(position = 0)

    data class RouteStopAddCard(
        val route: TransportRoute,
        val stop: TransportStop,
    ) : Card()

    data class EtaCard(
        val route: TransportRoute,
        val stop: TransportStop,
        val position: Int,
        val etaList: MutableList<TransportEta> = mutableListOf(),
        var platform: String? = "",
        val isValid: Boolean = true,
    ) : Card(), Comparable<EtaCard> {

        override fun compareTo(other: EtaCard): Int {
            return position.compareTo(other.position)
        }
    }
}
