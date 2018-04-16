package com.rcraggs.doubledose

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rcraggs.doubledose.di.appModule
import com.rcraggs.doubledose.di.deviceDBModule
import org.koin.android.ext.android.startKoin
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import com.rcraggs.doubledose.util.Constants


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin(this, listOf(deviceDBModule, appModule))

        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        val name = getString(R.string.channel_name)
        val description = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(Constants.CHANNEL_ID, name, importance)
        mChannel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}