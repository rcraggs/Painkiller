package com.rcraggs.doubledose

import com.rcraggs.doubledose.di.appModule
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugStatus
import com.rcraggs.doubledose.util.Constants
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
import org.threeten.bp.Instant

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testNoDosesMeansAvailable(){
        var status = DrugStatus("Ibroprufen")
        assertEquals(Constants.NEXT_DOSE_AVAILABLE, status.getTimeUnitNextDose())
    }

    @Test
    fun testDose2HoursAgoMeansAvailable(){
        var status = DrugStatus("Ibroprufen")
        status.timeOfLastDose = Instant.now().minusSeconds(Drug.getInstance().hoursBetweenDoses * 60 * 60)
        assertEquals(Constants.NEXT_DOSE_AVAILABLE, status.getTimeUnitNextDose())
    }

    @Test
    fun testDose1HourAgoMeansCountdown(){
        val status = DrugStatus("Ibroprufen")
        status.timeOfLastDose = Instant.now().minusSeconds(60 * 60)
        assert(status.getTimeUnitNextDose().endsWith('s'))
    }
}
