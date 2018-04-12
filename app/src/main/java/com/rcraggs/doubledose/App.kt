package com.rcraggs.doubledose

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rcraggs.doubledose.di.appModule
import com.rcraggs.doubledose.di.deviceDBModule
import org.koin.android.ext.android.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin(this, listOf(deviceDBModule, appModule))
    }
}