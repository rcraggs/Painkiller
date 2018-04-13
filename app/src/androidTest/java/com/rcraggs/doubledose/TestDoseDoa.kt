package com.rcraggs.doubledose

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.database.DoseDao
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugStatus
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.util.blockingObserve
import com.rcraggs.doubledose.util.dayAgo
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
    val paracetamol = Drug("Paracetamol")
    val ibroprufen = Drug("Ibroprufen")

    @Before
    fun createDB() {

        val context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java).build();
        doseDao = db.doseDao()
        AndroidThreeTen.init(context);

        // Insert drugs into the DB for testing
        paracetamol.id = db.drugDao().insert(paracetamol)
        ibroprufen.id = db.drugDao().insert(ibroprufen)
    }

    @After
    fun closeDb(){
        db.close()
    }

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
        val retrieved = doseDao.getLatest(paracetamol.id)
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
        val doses = doseDao.getDosesSince(paracetamol.id, get24HoursAgo())
        assertEquals(0, doses.size)
    }

    @Test
    fun testNoDosesOfSameTypeGivesNothingIn24Hours() {

        doseDao.insert(Dose(ibroprufen))
        val doses = doseDao.getDosesSince(paracetamol.id, get24HoursAgo())
        assertEquals(0, doses.size)
    }

    @Test
    fun testADoseJustOver24HoursGiveNothin() {

        val d = Dose(ibroprufen)
        d.taken = get24HoursAgo().minusSeconds(10)
        doseDao.insert(d)

        val doses = doseDao.getDosesSince(ibroprufen.id, get24HoursAgo())
        assertEquals(0, doses.size)
    }


    @Test
    fun testADoseJustUnder24HoursGiveOne() {

        val d = Dose(ibroprufen)
        d.taken = get24HoursAgo().plusSeconds(10)
        doseDao.insert(d)

        val doses = doseDao.getDosesSince(ibroprufen.id, get24HoursAgo())
        assertEquals(1, doses.size)
    }

    @Test
    fun testDeletingDrugDeletesDoses() {

        doseDao.insert(Dose(ibroprufen))
        doseDao.insert(Dose(ibroprufen))
        doseDao.insert(Dose(ibroprufen))
        doseDao.insert(Dose(ibroprufen))

        doseDao.insert(Dose(paracetamol))

        db.drugDao().delete(ibroprufen)

        val remainingDoses = doseDao.getAll()
        assertEquals(1, remainingDoses.size)
    }



    @Test
    fun testDose2HoursAgoMeansAvailable(){

        val d = Drug("Ibroprufen")

        val status = DrugStatus(d)

        val d1 = Dose(d)
        d1.taken = Instant.now().minusSeconds(60*60*d.gap)

        val doses = listOf(d1)
        status.refreshData(doses)

        assertEquals(Constants.NEXT_DOSE_AVAILABLE, status.getTimeUntilNextDose())
    }


    @Test
    fun testDose1HourAgoMeansCountdown(){
        val d = Drug("Ibroprufen")
        val status = DrugStatus(d)

        val d1 = Dose(d)
        d1.taken = Instant.now().minusSeconds(60*60)

        val doses = listOf(d1)
        status.refreshData(doses)

        assert(status.getTimeUntilNextDose().endsWith('s'))
    }

    private fun get24HoursAgo(): Instant {
        return Instant.now().minusSeconds(60 * 60 * 24)
    }

    @Test
    fun testCanGetDrugWithDoses() {

        val d = Drug("testdrug")
        d.id = db.drugDao().insert(d)

        db.doseDao().insert(Dose(d))
        db.doseDao().insert(Dose(d))
        db.doseDao().insert(Dose(d))

        val dd = db.drugDao().findWithDosesById(d.id)

        assertEquals(3, dd.doses.size)
    }

    @Test
    fun testCanGetDrugWithNoDoses() {

        val d = Drug("testdrug")
        d.id = db.drugDao().insert(d)
        val dd = db.drugDao().findWithDosesById(d.id)

        assertEquals(0, dd.doses.size)

    }
}
