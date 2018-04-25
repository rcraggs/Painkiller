package com.rcraggs.doubledose

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses
import com.rcraggs.doubledose.ui.getNextDrugToBecomeAvailable
import com.rcraggs.doubledose.util.INotificationsService
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class NotificationsTests {

    private lateinit var repo: AppRepo
    private val ns = Mockito.mock(INotificationsService::class.java)

    @Before
    fun setupRepo() {

        val context = InstrumentationRegistry.getTargetContext()
        val db = Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java).build()
        AndroidThreeTen.init(context)

        repo = AppRepo(db, ns)
    }

    @Test
    fun testNoNotificationsWhenThereAreNoDrugs() {

        verifyZeroInteractions(ns)
    }

    @Test
    fun testNotificationIsSetWhen1DrugIsPendingDI() {

        val now = Instant.now()

        val d1 = Drug("D1", 3, 10)
        val ds1 = DrugWithDoses(d1)
        ds1.doses = listOf(Dose(d1, now.minus(Duration.ofMinutes(5))))
        ds1.refreshData(now)

        val statusList = listOf(ds1)
        statusList.getNextDrugToBecomeAvailable()

        repo.rescheduleNotifications(statusList)
        verify(ns, times(1)).scheduleNotification(5 * 60, "D1")
    }


    @Test
    fun testNotificationIsSetWhen1DrugIsPendingDao() {

        val now = Instant.now()

        val d1 = Drug("D1", 3, 10)
        runBlocking {repo.insertDrug(d1)}

        val dose = Dose(d1, now.minus(Duration.ofMinutes(5)))
        runBlocking { repo.insertDose(dose) }

        verify(ns, times(1)).scheduleNotification(5 * 60, "D1")
    }

    @Test
    fun testNotificationIsSetWhen1DrugIsPendingAndAnotherIsMaxedDao() {

        val now = Instant.now()
        val d1 = Drug("D1", 3, 10)
        val d2 = Drug("D2", 1, 10)

        val dose2 = Dose(d2, now.minus(Duration.ofMinutes(12)))
        val dose = Dose(d1, now.minus(Duration.ofMinutes(5)))

        val j = launch {
            repo.insertDrug(d1)
            repo.insertDrug(d2)
        }

        val j2 = launch {
            j.join()
            repo.insertDose(dose2)
            repo.insertDose(dose)
        }

        launch {
            j2.join()
            verify(ns, times(2)).scheduleNotification(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString())
            verify(ns, times(1)).scheduleNotification(5 * 60, "D1")
        }
    }
}