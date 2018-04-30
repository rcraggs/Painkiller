package com.rcraggs.doubledose.util

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log
import android.os.Vibrator
import com.rcraggs.doubledose.R
import org.jetbrains.anko.defaultSharedPreferences


class NotificationPublisher: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val NOTIFICATION = "notification"
        const val SINGLE_NOTIFICATION_ID = 1000
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        // If the context is null there is nothing we can do here
        if (context == null)
            return

        // ONly show the notification if the user has not turned this off in the preferences.
        val prefs = context.defaultSharedPreferences
        if (prefs.getBoolean(context.getString(R.string.pref_show_notifications), true)){
            if (intent?.hasExtra(NOTIFICATION) == true){
                val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
                val id = intent.getIntExtra(NOTIFICATION_ID, 0);
                notificationManager.notify(id?: -1, notification);

                Log.d("NOTIFICATIONPUBLISHER", "Logging Broadcast")
            }
        }
    }
}
