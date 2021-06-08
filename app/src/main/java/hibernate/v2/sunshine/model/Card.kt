package hibernate.v2.sunshine.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.sunshine.db.eta.EtaEntity
import kotlinx.parcelize.Parcelize

sealed class Card {
    data class SettingsCard(
        @SerializedName("title")
        var title: String,
        @SerializedName("description")
        var description: String = "",
        @SerializedName("type")
        var type: Type,
        @SerializedName("icon")
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
}