package com.rcraggs.doubledose

import com.rcraggs.doubledose.di.appModule
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.koin.android.architecture.ext.viewModel
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest : KoinTest {

    @Before
    fun before() {
        startKoin(listOf(appModule))
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun testHistoryViewModel() {

        val vm: HistoryViewModel by inject()
        vm.start()

        assertEquals(vm.doses.size, 0)
    }
}
