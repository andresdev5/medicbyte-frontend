package ec.edu.espe.medicbyte.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val intent = Intent(context, BackgroundService::class.java)
        context.startService(intent)
        setAlarm(context)
    }

    fun setAlarm(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        assert(am != null)
        am.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            (System.currentTimeMillis() / 1000L + 15L) * 1000L,
            pi
        )
    }
}