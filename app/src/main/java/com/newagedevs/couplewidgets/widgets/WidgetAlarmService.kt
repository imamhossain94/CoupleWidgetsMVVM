package com.newagedevs.couplewidgets.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.newagedevs.couplewidgets.extensions.getMidnight

class WidgetAlarmService : Service() {

    private val alarmMgr: AlarmManager by lazy {
        getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent = getPendingIntent()
        val midnight = getMidnight().timeInMillis
        alarmMgr.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            midnight,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
//        val startTime = System.currentTimeMillis()
//        val interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES
//        alarmMgr.setInexactRepeating(
//            AlarmManager.RTC_WAKEUP,
//            startTime,
//            interval,
//            pendingIntent
//        )
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, CoupleWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or 0)
    }

}