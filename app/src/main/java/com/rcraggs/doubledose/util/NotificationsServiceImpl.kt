package com.rcraggs.doubledose.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.rcraggs.doubledose.R


interface INotificationsService {
    fun scheduleNotification(secondsToAvailable: Int, drugName: String)
    fun cancelNotifications()
}

/**
 * The places where we might have to re-schedule notifications
 * - adding a dose through the main activity
 * - editing a dose through the edit activity
 * - deleting a dose through the edit activity
 * - adjusting the properties of a drug
 * - an existing notification has triggered
 */
class NotificationsServiceImpl(private val context: Context, private val alarmManager: AlarmManager) : INotificationsService {

    override fun scheduleNotification(secondsToAvailable: Int, drugName: String) {

        // The notification object passed to be displayed
        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text) + " " + drugName)
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
                System.currentTimeMillis() + secondsToAvailable * 1000,
                pendingIntent
        )

        Log.d(this.javaClass.name, "Scheduled notification for $secondsToAvailable seconds")
    }

    override fun cancelNotifications() {

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


open class MockNotificationsService: INotificationsService {
    override fun scheduleNotification(secondsToAvailable: Int, drugName: String) {
        Log.d("MockNotificationsSer", "Logging Mock notification setup $secondsToAvailable for $drugName")
    }

    override fun cancelNotifications() {
        Log.d("MockNotificationsSer", "Cancelling all mock notifications")
    }

}