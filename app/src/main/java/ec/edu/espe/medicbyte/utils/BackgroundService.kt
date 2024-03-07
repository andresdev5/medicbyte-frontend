package ec.edu.espe.medicbyte.utils

import android.app.Service
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import android.widget.Toast


class BackgroundService : Service() {
    override fun onCreate() {
        val thread = HandlerThread(
            "ServiceStartArguments",
            android.os.Process.THREAD_PRIORITY_BACKGROUND
        )
        thread.start()
        Log.d("onCreate()", "After service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Freshly Made toast!", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
            return null
    }
}