package hibernate.v2.sunshine.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtil {
    fun getDate(
        dateStr: String?,
        fromPattern: String = DateFormat.ISO.value,
        timeZone: String = "GMT+8"
    ): Date? {
        try {
            if (!dateStr.isNullOrEmpty()) {
                val sdf = SimpleDateFormat(fromPattern, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone(timeZone)
                return sdf.parse(dateStr)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun formatString(
        dateStr: String?,
        toPattern: String,
        fromPattern: String = DateFormat.ISO.value,
        default: String = "",
        timeZone: String = "GMT+8"
    ): String {
        try {
            if (!dateStr.isNullOrEmpty()) {
                val sdf = SimpleDateFormat(fromPattern, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone(timeZone)
                sdf.parse(dateStr)?.let {
                    return it.formatString(toPattern)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return default
    }

    fun Date?.formatString(
        format: String,
        default: String = "",
        timeZone: String = "GMT+8"
    ): String {
        if (this != null) {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone(timeZone)
            return sdf.format(this)
        }

        return default
    }

    fun getTimeDiffInMin(date1: Date, date2: Date): Long {
        val diff: Long = date1.time - date2.time
        return TimeUnit.MILLISECONDS.toMinutes(diff)
    }
}

enum class DateFormat(val value: String) {
    HH_MM("HH:mm"),
    DD_MM_YY_HH_MM("dd-MM-yy HH:mm"),
    YYYY_MM_DD("yyyy-MM-dd"),
    ISO("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
    ISO_WITHOUT_MS("yyyy-MM-dd'T'HH:mm:ssZ"),
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    YYYY_MM_DD_HHMMSS("yyyyMMdd_HHmmss"),
    YYYY_MM_DD_CHINESE("yyyy年MM月dd日"),
    YYYY_MM("yyyy-MM"),
    YYYY_M_CHINESE("yyyy年M月"),
    YYYY_M_D("yyyy-M-d")
}
