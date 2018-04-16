package com.rcraggs.doubledose.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.ui.DrugStatus

class NotificationsService(repo: AppRepo, val alarmManager: AlarmManager) {

    fun scheduleNotification(context: Context, drugStatus: DrugStatus) {

        // The notification object passed to be displayed
        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text) + drugStatus.drug.name)
            .setSmallIcon(R.drawable.abc_ic_star_black_16dp)
                .build()

        // The intent that wraps up the notification that the alarm will broadcast
        val notificationIntent = Intent(context, NotificationPublisher::class.java)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, NotificationPublisher.SINGLE_NOTIFICATION_ID)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification)

        val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                notificationIntent ,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        //val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + drugStatus.minutesToNextDose * 60 * 1000,
                pendingIntent
        )

        Log.d(this.javaClass.name, "Scheduled notification for ${drugStatus.minutesToNextDose} time")
    }

    fun cancelNotifications(context: Context) {

        val myIntent = Intent(context, NotificationPublisher::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)

        Log.d(this.javaClass.name, "Cancelling notifications")
    }
}