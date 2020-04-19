package jp.co.tracecovid19.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import jp.co.tracecovid19.BlueTracePreference
import jp.co.tracecovid19.logger.DebugLogger

object Scheduler {
    private const val TAG = "Scheduler"

    fun scheduleServiceIntent(
        requestCode: Int,
        context: Context,
        intent: Intent,
        timeFromNowInMillis: Long
    ) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = PendingIntent.getService(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + timeFromNowInMillis, alarmIntent
            )

        } else {
            alarmMgr.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + timeFromNowInMillis, alarmIntent
            )
        }

    }

    fun scheduleRepeatingServiceIntent(
        requestCode: Int,
        context: Context,
        intent: Intent,
        intervalMillis: Long
    ) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = PendingIntent.getService(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        DebugLogger.log(
            TAG,
            "Purging alarm set to ${BlueTracePreference.getLastPurgeTime(context) + intervalMillis}"
        )
        alarmMgr.setRepeating(
            AlarmManager.RTC,
            BlueTracePreference.getLastPurgeTime(context) + intervalMillis,
            intervalMillis,
            alarmIntent
        )
    }

    fun cancelServiceIntent(requestCode: Int, context: Context, intent: Intent) {
        val alarmIntent =
            PendingIntent.getService(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmIntent.cancel()
    }
}
