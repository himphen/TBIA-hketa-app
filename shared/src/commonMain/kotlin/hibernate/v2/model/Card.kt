package hibernate.v2.model

import hibernate.v2.database.eta.SavedEtaEntity
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.eta.TransportEta
import hibernate.v2.model.transport.route.TransportRoute

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
        open val position: Long = 0,
    ) : Card(), Comparable<SettingsEtaCard> {
        override fun compareTo(other: SettingsEtaCard): Int {
            return position.compareTo(other.position)
        }
    }

    data class SettingsEtaItemCard(
        val entity: SavedEtaEntity,
        val route: TransportRoute,
        val stop: TransportStop,
        override val position: Long,
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
        val position: Long,
        val etaList: MutableList<TransportEta> = mutableListOf(),
        var platform: String? = "",
        val isValid: Boolean = true,
    ) : Card(), Comparable<EtaCard> {

        override fun compareTo(other: EtaCard): Int {
            return position.compareTo(other.position)
        }
    }
}
