package com.rcraggs.doubledose

import android.app.Application
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.di.appModule
import com.rcraggs.doubledose.di.inMemoryDBModule
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.util.blockingObserve
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.with
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.mock

class ViewModelTests : KoinTest {

    private val repo: AppRepo by inject()
    private val historyVM: HistoryViewModel by inject()
    private val homeViewModel: HomeViewModel by inject()

    @Before
    fun before() {
        closeKoin()
        startKoin(listOf(inMemoryDBModule, appModule)) with mock(Application::class.java)
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun testHistoryGetsAllDosesWhenNone() {

        historyVM.start()
        assertEquals(0, historyVM.doses.blockingObserve()!!.size)
    }

    @Test
    fun testHistoryGetsAllDosesTwoDifferentDrugs() {

        val drug = Drug("Test1")
        repo.insertDrug(drug)

        val drug2 = Drug("Test2")
        repo.insertDrug(drug2)

        // Add doses to the DB
        runBlocking {
            repo.insertDose(Dose(drug))
            repo.insertDose(Dose(drug2))
            repo.insertDose(Dose(drug2))
        }
        historyVM.start()
        assertEquals(3, historyVM.doses.blockingObserve()!!.size)
    }

    @Test
    fun testHistoryGetsAllDosesFor1Drug() {

        val drug = Drug("Test1")
        repo.insertDrug(drug)

        val drug2 = Drug("Test2")
        repo.insertDrug(drug2)

        // Add doses to the DB
        runBlocking {
            repo.insertDose(Dose(drug))
            repo.insertDose(Dose(drug2))
            repo.insertDose(Dose(drug2))
        }

        historyVM.start(drug.id)
        assertEquals(1, historyVM.doses.blockingObserve()!!.size)
    }

    @Test
    fun testGettingDrugsWhenNoneAreAdded() {
        assertEquals(0, homeViewModel.getDrugs().size)
    }

    @Test
    fun testGettingDrugsWhen2AreAdded() {

        val drug = Drug("Test1")
        repo.insertDrug(drug)

        val drug2 = Drug("Test2")
        repo.insertDrug(drug2)

        assertEquals(2, homeViewModel.getDrugs().size)
    }
}