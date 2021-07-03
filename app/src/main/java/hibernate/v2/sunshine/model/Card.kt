package hibernate.v2.sunshine.model

import hibernate.v2.api.model.Eta
import hibernate.v2.sunshine.db.eta.EtaEntity

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
        val entity: EtaEntity? = null,
        val route: TransportRoute? = null,
        val stop: TransportStop? = null,
        val type: Type,
    ) : Card() {
        enum class Type {
            DATA, INSERT_ROW
        }
    }

    data class RouteStopAddCard(
        val route: TransportRoute,
        val stop: TransportStop,
    ) : Card()

    data class EtaCard(
        val route: TransportRoute,
        val stop: TransportStop,
        var etaList: List<Eta> = listOf(),
    ) : Card()
}