package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.api.model.Eta
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.sunshine.db.eta.EtaEntity
import kotlinx.parcelize.Parcelize

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

    @Parcelize
    data class SettingsEtaCard(
        val entity: EtaEntity? = null,
        val route: Route? = null,
        val stop: Stop? = null,
        val type: Type
    ) : Parcelable, Card() {
        enum class Type {
            DATA, INSERT_ROW
        }
    }

    data class RouteStopCard(
        val route: Route,
        val stop: Stop
    ) : Card()

    @Parcelize
    data class RouteEtaStopCard(
        val entity: EtaEntity,
        val stop: Stop,
        var etaList: List<Eta>,
        val route: Route
    ) : Parcelable, Card()
}