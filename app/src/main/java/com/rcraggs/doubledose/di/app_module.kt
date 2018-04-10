package com.rcraggs.doubledose.di

import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val appModule : Module = applicationContext {

    factory { AppRepo(get()) }

    viewModel { HistoryViewModel(get()) }
    viewModel { HomeViewModel(get())}
}
