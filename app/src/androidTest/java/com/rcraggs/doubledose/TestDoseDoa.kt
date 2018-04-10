package com.rcraggs.doubledose

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.database.DoseDao
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.ui.DrugStatus
import com.rcraggs.doubledose.util.blockingObserve
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestDoseDoa {

    @Test
    fun useAppContext() {

        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.rcraggs.doubledose", appContext.packageName)
    }

    var db: AppDatabase by Delegates.notNull()
    var doseDao: DoseDao by Delegates.notNull()

    @Before
    fun createDB() {
        val context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java).build();
        doseDao = db.doseDao()
        AndroidThreeTen.init(context);
    }

    @After
    fun closeDb(){
        db.close()
    }

    private val paracetamol = "PARACETAMOL"
    private val ibroprufen = "IBROPRUFEN"

    @Test
    fun testAddThenDeleteLeavesNone() {

        val startNumber = doseDao.getAll().size

        val d = Dose(paracetamol)
        val insertedId = doseDao.insert(d)
        d.id = insertedId
        doseDao.delete(d)

        val endNumber = doseDao.getAll().size
        assertEquals(endNumber, startNumber)
    }

    @Test
    fun canSaveAndGetLatest() {

        val d = Dose(paracetamol)
        doseDao.insert(d)
        val retrieved = doseDao.getLatest(paracetamol)
        assertEquals(retrieved, d)
    }

    @Test
    fun testDeleteAll() {

        doseDao.insert(Dose(paracetamol))
        doseDao.insert(Dose(paracetamol))
        doseDao.insert(Dose(ibroprufen))

        doseDao.deleteAll()

        val endNumber = doseDao.getAll().size
        assertEquals(0, endNumber)
    }

    @Test
    fun canSaveAndGet3() {

        doseDao.insert(Dose(paracetamol))
        doseDao.insert(Dose(paracetamol))
        doseDao.insert(Dose(ibroprufen))

        val retrieved = doseDao.getAll()
        assertEquals(retrieved.size, 3)
    }

    @Test
    fun testGetLatestAnyType() {

        doseDao.insert(Dose(paracetamol))
        doseDao.insert(Dose(paracetamol))

        val d = Dose(ibroprufen)
        d.taken = d.taken.plusSeconds(1000)

        doseDao.insert(d)


        val retrieved = doseDao.getLatest().blockingObserve()
        assertEquals(d, retrieved)
    }

    @Test
    fun testNoDosesGivesNothingIn24Hours() {
        val doses = doseDao.getDosesSince(paracetamol, get24HoursAgo())
        assertEquals(0, doses.size)
    }

    @Test
    fun testNoDosesOfSameTypeGivesNothingIn24Hours() {

        doseDao.insert(Dose(ibroprufen))

        val doses = doseDao.getDosesSince(paracetamol, get24HoursAgo())
        assertEquals(0, doses.size)
    }

    @Test
    fun testADoseJustOver24HoursGiveNothin() {

        val d = Dose(ibroprufen)
        d.taken = get24HoursAgo().minusSeconds(10)
        doseDao.insert(d)

        val doses = doseDao.getDosesSince(ibroprufen, get24HoursAgo())
        assertEquals(0, doses.size)
    }


    @Test
    fun testADoseJustUnder24HoursGiveOne() {

        val d = Dose(ibroprufen)
        d.taken = get24HoursAgo().plusSeconds(10)
        doseDao.insert(d)

        val doses = doseDao.getDosesSince(ibroprufen, get24HoursAgo())
        assertEquals(1, doses.size)
    }


    @Test
    fun testLastDoseTakenYesterday(){

        val aTimeYesterday = Instant.now().minusSeconds(60*60*24)
        val ld1: LocalDateTime = LocalDateTime.ofInstant(aTimeYesterday, ZoneId.systemDefault())

        val status = DrugStatus(paracetamol)
        status.timeOfLastDose = aTimeYesterday
        val theTimeFormatterWithDate = DrugStatus.doseDateTimeFormatter.format(ld1)

        assertEquals(status.getTimeOfLastDoseInfo(), theTimeFormatterWithDate)
    }

    @Test
    fun testLastDoseTakenToday(){

        val aTimeToday = Instant.now()
        val ld1: LocalDateTime = LocalDateTime.ofInstant(aTimeToday, ZoneId.systemDefault())

        val status = DrugStatus(paracetamol)
        status.timeOfLastDose = aTimeToday
        val theTimeFormatterForTimeOnly = DrugStatus.doseTimeFormatter.format(ld1)

        assertEquals(status.getTimeOfLastDoseInfo(), theTimeFormatterForTimeOnly)
    }

    private fun get24HoursAgo(): Instant {
        return Instant.now().minusSeconds(60 * 60 * 24)
    }
}
