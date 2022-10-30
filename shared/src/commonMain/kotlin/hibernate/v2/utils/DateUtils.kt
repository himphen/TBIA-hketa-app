package hibernate.v2.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.time.DurationUnit

enum class DateFormatPattern(val value: String) {
    HH_MM("HH:mm"),
    HH_MM_12("h:mm a"),
    DD_MM_YY_HH_MM("dd-MM-yy HH:mm"),
    YYYY_MM_DD("yyyy-MM-dd"),
    ISO("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
    ISO_WITHOUT_MS("yyyy-MM-dd'T'HH:mm:ssZ"),
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    YYYY_MM_DD_HHMMSS("yyyyMMdd_HHmmss"),
    YYYY_MM_DD_CHINESE("yyyy年MM月dd日"),
    YYYY_MM("yyyy-MM"),
    YYYY_M_CHINESE("yyyy年M月"),
    YYYY_M_D("yyyy-M-d")
}

fun getTimeDiffInMin(date1: Instant, date2: Instant): Int {
    val diff = date1 - date2
    return diff.toInt(DurationUnit.MINUTES)
}

fun hongKongTimezone() = TimeZone.of("Asia/Hong_Kong")