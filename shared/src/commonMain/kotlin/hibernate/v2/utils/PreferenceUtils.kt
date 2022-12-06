package hibernate.v2.utils

expect fun KMMContext.putInt(key: String, value: Int)

expect fun KMMContext.getInt(key: String, default: Int): Int

expect fun KMMContext.putLong(key: String, value: Long)

expect fun KMMContext.getLong(key: String, default: Long): Long

expect fun KMMContext.putFloat(key: String, value: Float)

expect fun KMMContext.getFloat(key: String, default: Float): Float

expect fun KMMContext.putString(key: String, value: String?)

expect fun KMMContext.getString(key: String, default: String? = null): String?

expect fun KMMContext.putBool(key: String, value: Boolean)

expect fun KMMContext.getBool(key: String, default: Boolean): Boolean