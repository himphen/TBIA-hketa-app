package hibernate.v2.sunshine.core

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.gms.maps.model.LatLng
import hibernate.v2.sunshine.ui.eta.EtaCardViewType
import hibernate.v2.sunshine.util.PreferenceUtils

class SharedPreferencesManager(val context: Context) {

    companion object {
        const val PREF_ETA_CARD_TYPE = "PREF_ETA_CARD_TYPE"
        const val PREF_LAST_POSITION_LAT = "PREF_LAST_POSITION_LAT"
        const val PREF_LAST_POSITION_LNG = "PREF_LAST_POSITION_LNG"
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

    var lastLatLng: LatLng
        get() {
            val lat = preferences.getFloat(PREF_LAST_POSITION_LAT, 22.3111191f).toDouble()
            val lng = preferences.getFloat(PREF_LAST_POSITION_LNG, 114.1688018f).toDouble()

            return LatLng(lat, lng)
        }
        set(value) {
            preferences.edit {
                putFloat(PREF_LAST_POSITION_LAT, value.latitude.toFloat())
                putFloat(PREF_LAST_POSITION_LNG, value.longitude.toFloat())
                apply()
            }
        }
}