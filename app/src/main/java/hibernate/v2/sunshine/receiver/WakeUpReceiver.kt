package hibernate.v2.sunshine.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hibernate.v2.sunshine.ui.main.MainActivity
import java.util.Calendar

class WakeUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        if (today == Calendar.SATURDAY || today == Calendar.SUNDAY) return

        val scheduledIntent = Intent(context, MainActivity::class.java)
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(scheduledIntent)
    }
}