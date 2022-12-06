package hibernate.v2.core

import hibernate.v2.api.model.transport.Checksum
import hibernate.v2.model.transport.card.EtaCardViewType
import hibernate.v2.utils.KMMContext
import hibernate.v2.utils.LatLng
import hibernate.v2.utils.getBool
import hibernate.v2.utils.getFloat
import hibernate.v2.utils.getInt
import hibernate.v2.utils.getLong
import hibernate.v2.utils.getString
import hibernate.v2.utils.putBool
import hibernate.v2.utils.putFloat
import hibernate.v2.utils.putInt
import hibernate.v2.utils.putLong
import hibernate.v2.utils.putString
import kotlinx.serialization.json.Json

class SharedPreferencesManager(val context: KMMContext) {

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

    var etaCardType
        get() = EtaCardViewType.from(context.getInt(PREF_ETA_CARD_TYPE, 0))
        set(value) {
            context.putInt(PREF_ETA_CARD_TYPE, value.value)
        }

    var lastLatLng: LatLng
        get() {
            val lat = context.getFloat(PREF_LAST_POSITION_LAT, 22.31112f).toDouble()
            val lng = context.getFloat(PREF_LAST_POSITION_LNG, 114.16880f).toDouble()

            return LatLng(lat, lng)
        }
        set(value) {
            context.putFloat(PREF_LAST_POSITION_LAT, value.latitude.toFloat())
            context.putFloat(PREF_LAST_POSITION_LNG, value.longitude.toFloat())
        }

    var transportDataChecksum: Checksum?
        get() {
            val json = context.getString(PREF_TRANSPORT_DATA_CHECKSUM, null)
            if (json == null) return json
            return Json.decodeFromString(Checksum.serializer(), json)
        }
        set(value) {
            if (value == null) {
                context.putString(PREF_TRANSPORT_DATA_CHECKSUM, null)
            } else {
                val json = Json.encodeToString(Checksum.serializer(), value)
                context.putString(PREF_TRANSPORT_DATA_CHECKSUM, json)
            }
        }

    var trafficLayerToggle: Boolean
        get() = context.getBool(PREF_TRAFFIC_LAYER_TOGGLE, false)
        set(value) = context.putBool(PREF_TRAFFIC_LAYER_TOGGLE, value)

    var hideAdBannerUntil: Long
        get() = context.getLong(PREF_HIDE_AD_BANNER_UNTIL, 0)
        set(value) = context.putLong(PREF_HIDE_AD_BANNER_UNTIL, value)

    var defaultCompany: Int
        get() = context.getInt(PREF_DEFAULT_COMPANY, 0)
        set(value) = context.putInt(PREF_DEFAULT_COMPANY, value)

    var language: String
        get() = context.getString(PREF_LANGUAGE, "") ?: ""
        set(value) = context.putString(PREF_LANGUAGE, value)
}
