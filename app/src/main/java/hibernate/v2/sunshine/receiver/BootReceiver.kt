package hibernate.v2.sunshine.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hibernate.v2.sunshine.util.setupAlarm

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            context.setupAlarm()
        }
    }
}