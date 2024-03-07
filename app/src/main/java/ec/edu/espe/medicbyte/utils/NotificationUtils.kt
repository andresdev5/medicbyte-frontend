package ec.edu.espe.medicbyte.utils

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ec.edu.espe.medicbyte.R

class NotificationUtils {
    companion object {
        private val CHANNEL_ID = "MAIN_CHANNEL"
        private val CHANNEL_NAME = "MAIN_CHANNEL_NAME"
        private val NOTIF_ID = 1

        @Suppress("RemoteViewLayout")
        fun showViewNotification(context: Context, view: RemoteViews) {
            val intent: Intent = Intent(context, Activity::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val builder = Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_medicament)
                .setStyle(Notification.DecoratedCustomViewStyle())
                .setCustomContentView(view)
                .setContentIntent(pendingIntent)

            if (!isChannelCreated(context)) {
                createNotificationChannel(context)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (ActivityCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(0, builder.build())
            }
        }

        fun showNotification(context: Context, title: String, message: String, notificationId: Int = NOTIF_ID) {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_medicament)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            if (!isChannelCreated(context)) {
                createNotificationChannel(context)
            }

            val notificationManager = NotificationManagerCompat.from(context);

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            notificationManager.notify(notificationId, notification);
        }

        private fun isChannelCreated(context: Context): Boolean {
            val manager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            return manager.getNotificationChannel(CHANNEL_ID) != null
        }

        private fun createNotificationChannel(context: Context) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }

            val manager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}