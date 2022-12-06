package hibernate.v2.utils

import platform.Foundation.NSUserDefaults

actual fun KMMContext.putInt(key: String, value: Int) {
    NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), key)
}

actual fun KMMContext.getInt(key: String, default: Int): Int {
    return NSUserDefaults.standardUserDefaults.integerForKey(key).toInt()
}

actual fun KMMContext.putLong(key: String, value: Long) {
    NSUserDefaults.standardUserDefaults.setInteger(value, key)
}

actual fun KMMContext.getLong(key: String, default: Long): Long {
    return NSUserDefaults.standardUserDefaults.integerForKey(key)
}

actual fun KMMContext.putFloat(key: String, value: Float) {
    NSUserDefaults.standardUserDefaults.setFloat(value, key)
}

actual fun KMMContext.getFloat(key: String, default: Float): Float {
    return NSUserDefaults.standardUserDefaults.floatForKey(key)
}

actual fun KMMContext.putString(key: String, value: String?) {
    NSUserDefaults.standardUserDefaults.setObject(value, key)
}

actual fun KMMContext.getString(key: String, default: String?): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(key)
}

actual fun KMMContext.putBool(key: String, value: Boolean) {
    NSUserDefaults.standardUserDefaults.setBool(value, key)
}

actual fun KMMContext.getBool(key: String, default: Boolean): Boolean {
    return NSUserDefaults.standardUserDefaults.boolForKey(key)
}