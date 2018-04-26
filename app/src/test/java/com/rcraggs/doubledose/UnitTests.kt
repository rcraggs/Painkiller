package com.rcraggs.doubledose

import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.UiUtilities
import com.rcraggs.doubledose.ui.createWithDoses
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
        val status = Drug("Ibroprufen").createWithDoses(listOf(), nowInstant)

        val availString = UiUtilities.createDoseAvailableDesription(status.secondsBeforeNextDoseAvailable)
        assertEquals(Constants.NEXT_DOSE_AVAILABLE, availString)
    }

    @Test
    fun testRefreshDrugWithNoDoses() {
        val status = Drug("Ibroprufen").createWithDoses()
        status.refreshData(nowInstant)

        assertEquals(0, status.secondsBeforeNextDoseAvailable)
    }


    @Test
    fun testDoseNowLeaves24HoursToNextDose() {

        val drug = Drug("Ibroprufen", g = 60, p = 1)

        val pointInTime = Instant.parse("2000-01-01T00:00:00.00Z")
        val dd = drug.createWithDoses(listOf(pointInTime), pointInTime)

        assertEquals(24 * 60 * 60, dd.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun testDose25HoursAgoIsCleared() {

        val drug = Drug("Ibroprufen", g = 60, p = 1)
        val dd=  drug.createWithDoses(listOf(nowInstant.minus(Duration.ofHours(25))), nowInstant)

        assertEquals(0, dd.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun testDose119MinutesAgoCanDoseIn1Minute() {

        val drug = Drug("Ibroprufen", g = 120, p = 2)
        val dd=  drug.createWithDoses(listOf(nowInstant.minus(Duration.ofMinutes(119))), nowInstant)
        assertEquals(60, dd.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun testOverMaxDosesWaitsUntilLastDoseClears() {

        val drug = Drug("Ibroprufen", g = 60, p = 1)
        val dd=  drug.createWithDoses(listOf(nowInstant.minus(Duration.ofHours(1))), nowInstant)

        // There should be 23 hours before the next dose
        assertEquals(Duration.ofHours(23).seconds.toInt(), dd.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun testMultipleCausesOfUnavailDrugTakesLastCaust() {

        val d1 = Drug("D1", 3, 10)
        val dd =  d1.createWithDoses(listOf(
                nowInstant.minus(Duration.ofMinutes(5)),
                nowInstant.minus(Duration.ofMinutes(7))),
                nowInstant)

        val ds = listOf(dd).getNextDrugToBecomeAvailable()

        assertEquals(d1, ds?.drug)
        assertEquals(ds!!.secondsBeforeNextDoseAvailable, 5 * 60)
    }


    @Test
    fun testNextAvailOneReachedMaxAnotherPending() {

        val d1 = Drug("D1", 3, 10)
        val d2 = Drug("D2", 1, 10)

        val dd1=  d1.createWithDoses(listOf(
                nowInstant.minus(Duration.ofMinutes(5)),
                nowInstant.minus(Duration.ofMinutes(7))),
                nowInstant
        )

        val dd2 = d2.createWithDoses(listOf(nowInstant), nowInstant) // maxed out

        val ds = listOf(dd1, dd2).getNextDrugToBecomeAvailable()

        assertEquals(dd1, ds)
        assertEquals(5 * 60, dd1.secondsBeforeNextDoseAvailable)
    }


    @Test
    fun testNextAvailWhenMultipleDosesOverTheMax(){

        val dd = d1.createWithDoses(listOf(
                nowInstant.minus(Duration.ofHours(1)),
                nowInstant.minus(Duration.ofHours(19)),
                nowInstant.minus(Duration.ofHours(20)),
                nowInstant.minus(Duration.ofHours(21)),
                nowInstant.minus(Duration.ofHours(22)),
                nowInstant.minus(Duration.ofHours(23)),
                nowInstant.minus(Duration.ofHours(24))
                ), nowInstant)

        // It's going to take 23 hours before enough of those doses to clear
        assertEquals(10800, dd.secondsBeforeNextDoseAvailable)
    }

    @Test
    fun getTimeOfNextDoseAvailable() {

        val pointInTime = Instant.parse("2020-01-01T00:00:00.00Z")

        // Add a dose which will clear in one hour
        val dd = d1.createWithDoses(listOf(pointInTime.minus(Duration.ofMinutes(d1.gapMinutes-60)))
                , pointInTime)

        assertEquals(pointInTime.plusSeconds(Duration.ofHours(1).seconds), dd.timeNextDoseIsAvailable)
    }

    @Test
    fun testDosesIn24HoursWithAMixOfBeforeAndAfter() {

        val dd = d1.createWithDoses(listOf(
                nowInstant.minus(Duration.ofHours(27)),
                nowInstant.minus(Duration.ofHours(25)),
                nowInstant.minus(Duration.ofHours(24)).plusMillis(1),
                nowInstant.minus(Duration.ofHours(23)),
                nowInstant.minus(Duration.ofHours(22))),
                nowInstant
        )

        assertEquals(3, dd.dosesIn24Hours)
    }

    @Test
    fun testDosesIn24HoursReducesWithTime() {

        // Create a drug with 5 doses in 24 hours. One of which expires in 1 hour

        val dd = d1.createWithDoses(listOf(
                nowInstant.minus(Duration.ofHours(23)),
                nowInstant,
                nowInstant,
                nowInstant,
                nowInstant),
            nowInstant
        )

        assertEquals(5, dd.dosesIn24Hours) // right now

        dd.updateNextDoseAvailability(nowInstant.plus(Duration.ofHours(1)))

        assertEquals(4, dd.dosesIn24Hours) // in 1 hour's time
    }
}
