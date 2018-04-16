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

    val repo: AppRepo by inject()
    val historyVM: HistoryViewModel by inject()
    val homeViewModel: HomeViewModel by inject()

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
        drug.id = repo.db.drugDao().insert(drug)

        val drug2 = Drug("Test2")
        drug2.id = repo.db.drugDao().insert(drug2)

        // Add doses to the DB
        repo.db.doseDao().insert(Dose(drug))
        repo.db.doseDao().insert(Dose(drug2))
        repo.db.doseDao().insert(Dose(drug2))

        historyVM.start()
        assertEquals(3, historyVM.doses.blockingObserve()!!.size)
    }

    @Test
    fun testHistoryGetsAllDosesFor1Drug() {

        val drug = Drug("Test1")
        drug.id = repo.db.drugDao().insert(drug)

        val drug2 = Drug("Test2")
        drug2.id = repo.db.drugDao().insert(drug2)


        // Add doses to the DB
        repo.db.doseDao().insert(Dose(drug))
        repo.db.doseDao().insert(Dose(drug2))
        repo.db.doseDao().insert(Dose(drug2))

        historyVM.start(drug.id)
        assertEquals(1, historyVM.doses.blockingObserve()!!.size)
    }


    /**
    start
    getStatuses
    updateAllDrugStatuses
    takeDose
    takeDose
     */

    @Test
    fun testGettingDrugsWhenNoneAreAdded() {
        assertEquals(0, homeViewModel.getDrugs().size)
    }


    @Test
    fun testGettingDrugsWhen2AreAdded() {

        val drug = Drug("Test1")
        drug.id = repo.db.drugDao().insert(drug)

        val drug2 = Drug("Test2")
        drug2.id = repo.db.drugDao().insert(drug2)

        assertEquals(2, homeViewModel.getDrugs().size)
    }
//
//    @Test
//    fun testTakingADoseNotifiesObserver() {
//
//        val drug = Drug("Test1")
//        drug.id = repo.db.drugDao().insert(drug)
//
//        val observer = mock<Observer<List<DrugStatus>>>()
//
//        // Set up the model and observe the drugs
//        homeViewModel.start()
//        homeViewModel.getStatuses().observeForever(observer)
//
//        // Take a dose
//        homeViewModel.takeDose(drug)
//
//        // Check that the observer was notified
//        verify(observer, times(1)).onChanged(any())
//
//    }
}