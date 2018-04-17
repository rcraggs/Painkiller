package com.rcraggs.doubledose

import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugStatus
import com.rcraggs.doubledose.ui.UiUtilities
import com.rcraggs.doubledose.ui.getNextDrugToBecomeAvailable
import com.rcraggs.doubledose.util.Constants
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.Duration
import org.threeten.bp.Instant

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {

    @Test
    fun testNoDosesMeansAvailable(){
        val status = DrugStatus(Drug("Ibroprufen"))
        status.updateNextDoseAvailability(Instant.now())

        val availString = UiUtilities.createDoseAvailableDesription(status.secondsBeforeNextDoseAvailable)
        assertEquals(Constants.NEXT_DOSE_AVAILABLE, availString)
    }

    @Test
    fun testRefreshDrugWithNoDoses() {
        val status = DrugStatus(Drug("Ibroprufen"))
        val doses = ArrayList<Dose>()

        status.refreshData(doses, Instant.now())

        assertEquals(null, status.timeOfLastDose)
        assertEquals(0, status.secondsBeforeNextDoseAvailable)
    }


    @Test
    fun testDoseNowLeaves24HoursToNextDose() {

        val drug = Drug("Ibroprufen", g = 60, p = 1)
        val status = DrugStatus(drug)

        val pointInTime = Instant.parse("2000-01-01T00:00:00.00Z")

        val doses = listOf(Dose(drug, pointInTime))
        status.refreshData(doses, pointInTime)

        assertEquals(24 * 60 * 60, status.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun testDose25HoursAgoIsCleared() {

        val drug = Drug("Ibroprufen", g = 60, p = 1)
        val status = DrugStatus(drug)

        val pointInTime = Instant.parse("2000-01-01T00:00:00.00Z")
        val a25HoursAgo = pointInTime.minus(Duration.ofHours(25))

        val doses = listOf(Dose(drug, a25HoursAgo))
        status.refreshData(doses, pointInTime)

        assertEquals(0, status.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun testDose119MinutesAgoCanDoseIn1Minute() {


        val drug = Drug("Ibroprufen", g = 120, p = 2)
        val status = DrugStatus(drug)

        val pointInTime = Instant.parse("2000-01-01T00:00:00.00Z")

        val doses = listOf(Dose(drug, pointInTime.minus(Duration.ofMinutes(119))))
        status.refreshData(doses, pointInTime)

        assertEquals(60, status.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun testOverMaxDosesWaitsUntilLastDoseClears() {

        val drug = Drug("Ibroprufen", g = 60, p = 1)
        val status = DrugStatus(drug)

        // Set up a dose which is one hour ago but we are only allowed one per 24 hours
        val currentTimeSeconds = 10000L
        val secondAgoLastDose = 10L * 60L // one hour go
        val doseTime = Instant.ofEpochSecond(currentTimeSeconds - secondAgoLastDose)
        val currentTime = Instant.ofEpochSecond(currentTimeSeconds)
        val doses = listOf(Dose(drug, doseTime))

        status.refreshData(doses, currentTime)

        assertEquals(status.secondsBeforeNextDoseAvailable, ((60 * 24 * 60) - secondAgoLastDose).toInt())
    }

    @Test
    fun testMultipleCausesOfUnavailDrugTakesLastCaust() {

        val now = Instant.now()

        val d1 = Drug("D1", 3, 10)
        val ds1 = DrugStatus(d1)

        ds1.refreshData(listOf(
                Dose(d1, now.minus(Duration.ofMinutes(5))),
                Dose(d1, now.minus(Duration.ofMinutes(7)))
        ), now)

        val statusList = listOf(ds1)
        val ds = statusList.getNextDrugToBecomeAvailable()

        assertEquals(ds1, ds)
        assertEquals(ds1.secondsBeforeNextDoseAvailable, 5 * 60)
    }


    @Test
    fun testNextAvailOneReachedMaxAnotherPending() {

        val now = Instant.now()

        val d1 = Drug("D1", 3, 10)
        val d2 = Drug("D2", 1, 10)

        val ds1 = DrugStatus(d1)
        val ds2 = DrugStatus(d2)

        ds2.refreshData(listOf(Dose(d2, now))) // Max out of D2

        ds1.refreshData(listOf(
                Dose(d1, now.minus(Duration.ofMinutes(5))),
                Dose(d1, now.minus(Duration.ofMinutes(7)))
        ), now)

        val statusList = listOf(ds1, ds2)
        val ds = statusList.getNextDrugToBecomeAvailable()

        assertEquals(ds1, ds)
        assertEquals(ds1.secondsBeforeNextDoseAvailable, 5 * 60)
    }

}
