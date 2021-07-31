package hibernate.v2.sunshine.util

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import hibernate.v2.sunshine.receiver.WakeUpReceiver
import java.util.Calendar
import kotlin.math.roundToInt
import android.util.DisplayMetrics




/**
 * Returns true when [Context] is unavailable or is about to become unavailable
 */
fun Context?.isDoomed(): Boolean = when (this) {
    null -> true
    is Application -> false
    is Activity -> (this.isDestroyed or this.isFinishing)
    else -> false
}

fun Context.setupAlarm() {
    val intent = Intent(this, WakeUpReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        10000,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val calNow = Calendar.getInstance()
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 5)
        set(Calendar.SECOND, 0)

        if (compareTo(calNow) <= 0) {
            add(Calendar.DATE, 1)
        }
    }
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

fun Context.cancelAlarm() {
    val intent = Intent(this, WakeUpReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        10000,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
    )
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
}

fun dpToPx(dp: Float): Int {
    return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
}

fun pxToDp(px: Int): Int {
    return (px / (Resources.getSystem().displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

inline fun <reified T : Enum<T>> Bundle.getEnum(key: String, default: T) =
    getInt(key).let { if (it >= 0) enumValues<T>()[it] else default }

fun <T : Enum<T>> Bundle.putEnum(key: String, value: T?) =
    putInt(key, value?.ordinal ?: -1)