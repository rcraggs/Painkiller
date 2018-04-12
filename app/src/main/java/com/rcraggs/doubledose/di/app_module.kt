package com.rcraggs.doubledose.di

import android.arch.persistence.room.Room
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.database.AppDbCallback
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.context.Context
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val appModule : Module = applicationContext {

    factory { AppRepo(get()) }

    viewModel { HistoryViewModel(get()) }
    viewModel { HomeViewModel(get())}
}


val inMemoryDBModule : Module = applicationContext {
    bean { }
    bean { createInMemoryAppDatabase(this)}
}

val deviceDBModule : Module = applicationContext {
    bean { createActualAppDatabase(this) }
}


fun createInMemoryAppDatabase(context: Context): AppDatabase {
    return Room.inMemoryDatabaseBuilder(context.androidApplication(), AppDatabase::class.java)
            .build();
}

fun createActualAppDatabase(context: Context): AppDatabase {
    return Room
        .databaseBuilder(context.androidApplication(), AppDatabase::class.java, Constants.PROD_DB_NAME)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .addCallback(AppDbCallback())
        .build()
}