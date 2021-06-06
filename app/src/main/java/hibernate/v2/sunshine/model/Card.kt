package hibernate.v2.sunshine.model

import android.content.Context
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.sunshine.db.eta.EtaEntity

sealed class Card {
    data class SettingsCard(
        @SerializedName("title")
        var title: String,
        @SerializedName("description")
        var description: String = "",
        @SerializedName("type")
        var type: Type,
        @SerializedName("localImageResource")
        var localImageResourceName: String,
    ) : Card() {
        fun getLocalImageResourceId(context: Context): Int {
            return context.resources.getIdentifier(
                localImageResourceName, "drawable",
                context.packageName
            )
        }

        enum class Type {
            ETA, TRAFFIC, NONE
        }
    }

    data class SettingsEtaCard(
        val entity: EtaEntity,
        var route: Route,
        var stop: Stop
    ) : Card() {
    }
}