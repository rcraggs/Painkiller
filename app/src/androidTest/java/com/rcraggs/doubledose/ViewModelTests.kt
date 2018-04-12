package com.rcraggs.doubledose

import com.rcraggs.doubledose.di.appModule
import com.rcraggs.doubledose.di.inMemoryDBModule
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

class ViewModelTests : KoinTest {

    val historyVM: HistoryViewModel by inject()

    @Before
    fun before() {
        loadKoinModules(inMemoryDBModule)
        loadKoinModules(appModule)
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun testHistoryGetsAllDoses() {

        historyVM.start()
        assertEquals(0, historyVM.doses.size)
    }
}