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
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 10)
        set(Calendar.SECOND, 0)
    }
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        getAlarmIntent(this)
    )
}

fun Context.cancelAlarm() {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(getAlarmIntent(this))
}

fun getAlarmIntent(context: Context): PendingIntent? {
    val intent = Intent(context, WakeUpReceiver::class.java)
    return PendingIntent.getBroadcast(context, 10000, intent, PendingIntent.FLAG_UPDATE_CURRENT)

}