package hibernate.v2.utils

import androidx.preference.PreferenceManager

actual fun KMMContext.putInt(key: String, value: Int) {
    getSharedPrefsEditor().putInt(key, value).apply()
}

actual fun KMMContext.getInt(key: String, default: Int): Int {
    return getSharedPrefs().getInt(key, default)
}

actual fun KMMContext.putLong(key: String, value: Long) {
    getSharedPrefsEditor().putLong(key, value).apply()
}

actual fun KMMContext.getLong(key: String, default: Long): Long {
    return getSharedPrefs().getLong(key, default)
}

actual fun KMMContext.putFloat(key: String, value: Float) {
    getSharedPrefsEditor().putFloat(key, value).apply()
}

actual fun KMMContext.getFloat(key: String, default: Float): Float {
    return getSharedPrefs().getFloat(key, default)
}

actual fun KMMContext.putString(key: String, value: String?) {
    getSharedPrefsEditor().putString(key, value).apply()
}

actual fun KMMContext.getString(key: String, default: String?): String? {
    return getSharedPrefs().getString(key, default)
}

actual fun KMMContext.putBool(key: String, value: Boolean) {
    getSharedPrefsEditor().putBoolean(key, value).apply()
}

actual fun KMMContext.getBool(key: String, default: Boolean): Boolean {
    return getSharedPrefs().getBoolean(key, default)
}

private fun KMMContext.getSharedPrefs() = PreferenceManager.getDefaultSharedPreferences(this)
private fun KMMContext.getSharedPrefsEditor() = getSharedPrefs().edit()