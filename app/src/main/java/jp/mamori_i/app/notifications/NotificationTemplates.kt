package jp.mamori_i.app.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import jp.mamori_i.app.R
import jp.mamori_i.app.screen.home.BLEActivity
import jp.mamori_i.app.screen.home.TestBLEActivity
import jp.mamori_i.app.service.BluetoothMonitoringService.Companion.PENDING_ACTIVITY
import jp.mamori_i.app.service.BluetoothMonitoringService.Companion.PENDING_WIZARD_REQ_CODE

class NotificationTemplates {

    companion object {

        fun getStartupNotification(context: Context, channel: String): Notification {

            val builder = NotificationCompat.Builder(context, channel)
                .setContentText("Tracer is setting up its antennas")
                .setContentTitle("Setting things up")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setSound(null)
                .setVibrate(null)
                .setColor(ContextCompat.getColor(context, R.color.notification_tint))

            return builder.build()
        }

        fun getRunningNotification(context: Context, channel: String): Notification {

            var intent = Intent(context, TestBLEActivity::class.java)

            val activityPendingIntent = PendingIntent.getActivity(
                context, PENDING_ACTIVITY,
                intent, 0
            )

            val builder = NotificationCompat.Builder(context, channel)
                .setContentTitle(context.getText(R.string.service_ok_title))
                .setContentText(context.getText(R.string.service_ok_body))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(activityPendingIntent)
                .setTicker(context.getText(R.string.service_ok_body))
                .setStyle(NotificationCompat.BigTextStyle().bigText(context.getText(R.string.service_ok_body)))
                .setWhen(System.currentTimeMillis())
                .setSound(null)
                .setVibrate(null)
                .setColor(ContextCompat.getColor(context, R.color.notification_tint))

            return builder.build()
        }

        fun lackingThingsNotification(context: Context, channel: String): Notification {
            // TODO: このまま使用するなら、ホーム画面に遷移すべき
            val intent = Intent(context, BLEActivity::class.java)

            val activityPendingIntent = PendingIntent.getActivity(
                context, PENDING_WIZARD_REQ_CODE,
                intent, 0
            )

            val builder = NotificationCompat.Builder(context, channel)
                .setContentTitle(context.getText(R.string.service_not_ok_title))
                .setContentText(context.getText(R.string.service_not_ok_body))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker(context.getText(R.string.service_not_ok_body))
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getText(R.string.service_not_ok_action),
                    activityPendingIntent
                )
                .setContentIntent(activityPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSound(null)
                .setVibrate(null)
                .setColor(ContextCompat.getColor(context, R.color.notification_tint))

            return builder.build()
        }
    }
}
