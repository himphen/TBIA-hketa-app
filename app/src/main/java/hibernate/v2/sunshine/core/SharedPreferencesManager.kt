package hibernate.v2.sunshine.core

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import hibernate.v2.sunshine.ui.eta.EtaCardViewType
import hibernate.v2.sunshine.util.PreferenceUtils

class SharedPreferencesManager(val context: Context) {

    companion object {
        const val PREF_ETA_CARD_TYPE = "PREF_ETA_CARD_TYPE"
    }

    private val preferences: SharedPreferences = PreferenceUtils.sharedPrefs(context)

    var etaCardType: EtaCardViewType
        get() = EtaCardViewType.from(preferences.getInt(PREF_ETA_CARD_TYPE, 0))
        set(value) {
            preferences.edit {
                putInt(PREF_ETA_CARD_TYPE, value.value)
                apply()
            }
        }
}