package hibernate.v2.sunshine.util

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import hibernate.v2.sunshine.receiver.WakeUpReceiver
import java.util.Calendar

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