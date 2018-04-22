package com.rcraggs.doubledose.di

import android.app.AlarmManager
import android.arch.persistence.room.Room
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.database.AppDbCallback
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.util.INotificationsService
import com.rcraggs.doubledose.util.MockNotificationsService
import com.rcraggs.doubledose.util.NotificationsServiceImpl
import com.rcraggs.doubledose.viewmodel.DoseEditViewModel
import com.rcraggs.doubledose.viewmodel.DrugAdminViewModel
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.context.Context
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val appModule : Module = applicationContext {

    factory { AppRepo(get(), get()) }

    viewModel { HistoryViewModel(get()) }
    viewModel { HomeViewModel(get(), get())}
    viewModel { DoseEditViewModel(get(), get())}
    viewModel { DrugAdminViewModel(get(), get())}
    bean {getAlarmManager(this)}
}

val inMemoryDBModule : Module = applicationContext {

    bean { createMockNotificationService() as INotificationsService}
    bean { createInMemoryAppDatabase(this)}
}

val deviceDBModule : Module = applicationContext {
    bean { createActualAppDatabase(this) }
    bean {NotificationsServiceImpl(get(), get()) as INotificationsService}
}

fun getContext(context: Context): android.content.Context {
    return context.androidApplication()
}
fun getAlarmManager(context: Context): AlarmManager {
    return context.androidApplication()
            .getSystemService(android.content.Context.ALARM_SERVICE) as AlarmManager
}

fun createMockNotificationService(): INotificationsService {
    return MockNotificationsService()
}

fun createInMemoryAppDatabase(context: Context): AppDatabase {
    return Room.inMemoryDatabaseBuilder(context.androidApplication(), AppDatabase::class.java)
            .build();
}

fun createActualAppDatabase(context: Context): AppDatabase {
    return Room
        .databaseBuilder(context.androidApplication(), AppDatabase::class.java, Constants.PROD_DB_NAME)
        //.allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .addCallback(AppDbCallback())
        .build()
}