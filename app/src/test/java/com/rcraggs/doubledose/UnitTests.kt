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

    private val nowInstant: Instant = Instant.now()

    private val d1: Drug by lazy {
        val d = Drug("D1", 4, 4 * 60)
        d
    }

    @Test
    fun testNoDosesMeansAvailable(){
        val status = DrugStatus(Drug("Ibroprufen"))
        status.updateNextDoseAvailability(nowInstant)

        val availString = UiUtilities.createDoseAvailableDesription(status.secondsBeforeNextDoseAvailable)
        assertEquals(Constants.NEXT_DOSE_AVAILABLE, availString)
    }

    @Test
    fun testRefreshDrugWithNoDoses() {
        val status = DrugStatus(Drug("Ibroprufen"))
        val doses = ArrayList<Dose>()

        status.refreshData(doses, nowInstant)

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

        // Dose 1 hour ago
        status.refreshData(listOf(Dose(drug, nowInstant.minus(Duration.ofHours(1)))), nowInstant)

        // There should be 23 hours before the next dose
        assertEquals(Duration.ofHours(23).seconds.toInt(), status.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun testMultipleCausesOfUnavailDrugTakesLastCaust() {



        val d1 = Drug("D1", 3, 10)
        val ds1 = DrugStatus(d1)

        ds1.refreshData(listOf(
                Dose(d1, nowInstant.minus(Duration.ofMinutes(5))),
                Dose(d1, nowInstant.minus(Duration.ofMinutes(7)))
        ), nowInstant)

        val statusList = listOf(ds1)
        val ds = statusList.getNextDrugToBecomeAvailable()

        assertEquals(ds1, ds)
        assertEquals(ds1.secondsBeforeNextDoseAvailable, 5 * 60)
    }


    @Test
    fun testNextAvailOneReachedMaxAnotherPending() {



        val d1 = Drug("D1", 3, 10)
        val d2 = Drug("D2", 1, 10)

        val ds1 = DrugStatus(d1)
        val ds2 = DrugStatus(d2)

        ds2.refreshData(listOf(Dose(d2, nowInstant))) // Max out of D2

        ds1.refreshData(listOf(
                Dose(d1, nowInstant.minus(Duration.ofMinutes(5))),
                Dose(d1, nowInstant.minus(Duration.ofMinutes(7)))
        ), nowInstant)

        val statusList = listOf(ds1, ds2)
        val ds = statusList.getNextDrugToBecomeAvailable()

        assertEquals(ds1, ds)
        assertEquals(ds1.secondsBeforeNextDoseAvailable, 5 * 60)
    }





    @Test
    fun testNextAvailWhenMultipleDosesOverTheMax(){

        val ds = DrugStatus(d1)

        ds.refreshData(listOf(
                Dose(d1, nowInstant.minus(Duration.ofHours(1))),
                Dose(d1, nowInstant.minus(Duration.ofHours(19))),
                Dose(d1, nowInstant.minus(Duration.ofHours(20))),
                Dose(d1, nowInstant.minus(Duration.ofHours(21))),
                Dose(d1, nowInstant.minus(Duration.ofHours(22))),
                Dose(d1, nowInstant.minus(Duration.ofHours(23))),
                Dose(d1, nowInstant.minus(Duration.ofHours(24)))
                ))

        // It's going to take 23 hours before enough of those doses to clear
        assertEquals(10800, ds.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun getTimeOfNextDoseAvailable() {

        val pointInTime = Instant.parse("2020-01-01T00:00:00.00Z")

        val ds = DrugStatus(d1)

        // Add a dose which will clear in one hour
        ds.refreshData(listOf(Dose(d1, pointInTime.minus(Duration.ofMinutes(d1.gapMinutes-60)))), pointInTime)

        assertEquals(pointInTime.plusSeconds(Duration.ofHours(1).seconds), ds.timeNextDoseIsAvailable)
    }

    @Test
    fun testDosesIn24HoursReducesWithTime() {

        // Create a drug with 5 doses in 24 hours. One of which expires in 1 hour
        val ds = DrugStatus(d1)
        ds.refreshData(listOf(
            Dose(d1, nowInstant.minus(Duration.ofHours(23))),
                Dose(d1, nowInstant),
                Dose(d1, nowInstant),
                Dose(d1, nowInstant),
                Dose(d1, nowInstant)
        ))

        assertEquals(5, ds.dosesIn24Hours) // right now

        ds.updateNextDoseAvailability(nowInstant.plus(Duration.ofHours(1)))

        assertEquals(4, ds.dosesIn24Hours) // in 1 hour's time
    }
}
