package hibernate.v2.sunshine.core

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import hibernate.v2.api.model.transport.Checksum
import hibernate.v2.sunshine.ui.bookmark.EtaCardViewType
import hibernate.v2.sunshine.util.PreferenceUtils

class SharedPreferencesManager(val context: Context) {

    companion object {
        const val PREF_TRANSPORT_DATA_CHECKSUM = "PREF_TRANSPORT_DATA_CHECKSUM"

        const val PREF_ETA_CARD_TYPE = "PREF_ETA_CARD_TYPE"
        const val PREF_LAST_POSITION_LAT = "PREF_LAST_POSITION_LAT"
        const val PREF_LAST_POSITION_LNG = "PREF_LAST_POSITION_LNG"
        const val PREF_TRAFFIC_LAYER_TOGGLE = "PREF_TRAFFIC_LAYER_TOGGLE"
        const val PREF_HIDE_AD_BANNER_UNTIL = "PREF_HIDE_AD_BANNER_UNTIL"
        const val PREF_DEFAULT_COMPANY = "PREF_DEFAULT_COMPANY"
        const val PREF_LANGUAGE = "PREF_LANGUAGE"
    }

    private val preferences: SharedPreferences = PreferenceUtils.sharedPrefs(context)

    var etaCardType
        get() = EtaCardViewType.from(preferences.getInt(PREF_ETA_CARD_TYPE, 0))
        set(value) = preferences.edit {
            putInt(PREF_ETA_CARD_TYPE, value.value)
            apply()
        }

    var lastLatLng: LatLng
        get() {
            val lat = preferences.getFloat(PREF_LAST_POSITION_LAT, 22.31112f).toDouble()
            val lng = preferences.getFloat(PREF_LAST_POSITION_LNG, 114.16880f).toDouble()

            return LatLng(lat, lng)
        }
        set(value) = preferences.edit {
            putFloat(PREF_LAST_POSITION_LAT, value.latitude.toFloat())
            putFloat(PREF_LAST_POSITION_LNG, value.longitude.toFloat())
            apply()
        }

    var transportDataChecksum: Checksum?
        get() {
            val json = preferences.getString(PREF_TRANSPORT_DATA_CHECKSUM, null)
            if (json == null) return json
            return Gson().fromJson(json, Checksum::class.java)
        }
        set(value) {
            val json = Gson().toJson(value)
            preferences.edit {
                putString(PREF_TRANSPORT_DATA_CHECKSUM, json)
                apply()
            }
        }

    var trafficLayerToggle: Boolean
        get() = preferences.getBoolean(PREF_TRAFFIC_LAYER_TOGGLE, false)
        set(value) = preferences.edit {
            putBoolean(PREF_TRAFFIC_LAYER_TOGGLE, value)
            apply()
        }

    var hideAdBannerUntil: Long
        get() = preferences.getLong(PREF_HIDE_AD_BANNER_UNTIL, 0)
        set(value) = preferences.edit {
            putLong(PREF_HIDE_AD_BANNER_UNTIL, value)
            commit()
        }

    var defaultCompany: Int
        get() = preferences.getInt(PREF_DEFAULT_COMPANY, 0)
        set(value) = preferences.edit {
            putInt(PREF_DEFAULT_COMPANY, value)
            commit()
        }

    var language: String
        get() = preferences.getString(PREF_LANGUAGE, "") ?: ""
        set(value) {
            preferences.edit {
                putString(PREF_LANGUAGE, value)
                apply()
            }
        }
}
