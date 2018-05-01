package com.rcraggs.doubledose

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.database.DoseDao
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses
import com.rcraggs.doubledose.ui.UiUtilities
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.util.MockNotificationsService
import com.rcraggs.doubledose.util.blockingObserve
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import kotlin.properties.Delegates


@RunWith(AndroidJUnit4::class)
class TestRepo {

    @Test
    fun useAppContext() {

        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.rcraggs.doubledose", appContext.packageName)
    }

    private var db: AppDatabase by Delegates.notNull()
    private var doseDao: DoseDao by Delegates.notNull()
    private lateinit var repo: AppRepo

    private val paracetamol = Drug("Paracetamol")
    private val ibroprufen = Drug("Ibroprufen")

    @Before
    fun setupRepo() {

        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java).build()
        doseDao = db.doseDao()
        AndroidThreeTen.init(context)

        val notifications = MockNotificationsService()
        repo = AppRepo(db, notifications)

        // Insert drugs into the DB for testing
        runBlocking {
            paracetamol.id = db.drugDao().insert(paracetamol)
            ibroprufen.id = db.drugDao().insert(ibroprufen)
        }
    }

    @After
    fun closeDb(){
        db.close()
    }

    // todo replace with mock repo
//    @Test
//    fun testAddThenDeleteLeavesNone() {
//
//        val startNumber = doseDao.getAll().size
//
//        val d = Dose(paracetamol)
//        val insertedId = doseDao.insert(d)
//        d.id = insertedId
//        doseDao.delete(d)
//
//        val endNumber = doseDao.getAll().size
//        assertEquals(endNumber, startNumber)
//    }

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


        val j = launch {
            doseDao.insert(Dose(paracetamol))
            doseDao.insert(Dose(paracetamol))
            val d = Dose(ibroprufen)
            d.taken = d.taken.plusSeconds(1000)
            doseDao.insert(d)
        }

        launch {
            j.join()
            val retrieved = doseDao.getLatest().blockingObserve()
            assertEquals(ibroprufen.id, retrieved?.drugId)
        }

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

        val status = DrugWithDoses(d)

        val d1 = Dose(d)
        d1.taken = Instant.now().minusSeconds(60*60*d.gapMinutes)

        status.doses = listOf(d1)
        status.refreshData()

        val availString = UiUtilities.createDoseAvailabilityDescription(status)
        assertEquals(Constants.NEXT_DOSE_AVAILABLE, availString)
    }


    @Test
    fun testDose1HourAgoMeansCountdown(){
        val d = Drug("Ibroprufen")
        val status = DrugWithDoses(d)

        val d1 = Dose(d)
        d1.taken = Instant.now().minusSeconds(60*60)

        status.doses = listOf(d1)
        status.refreshData()

        assertNotEquals(UiUtilities.createDoseAvailabilityDescription(status),
                Constants.NEXT_DOSE_AVAILABLE)
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

    @Test
    fun testNextAvailableWithNoDoses() {
        val emptyDSList = ArrayList<DrugWithDoses>()
        val ds = repo.getNextUnavailableDrugToBecomeAvailable(emptyDSList)
        assertNull(ds)
    }

    @Test
    fun testNextAvailableWithAllDosesAreInThePast() {

        val now = Instant.now()

        val d1 = Drug("D1", 2, 10)
        val d2 = Drug("D2", 2, 10)

        val ds1 = DrugWithDoses(d1)
        ds1.doses = listOf(Dose(d1, now.minus(Duration.ofMinutes(11))))
        ds1.refreshData()

        val ds2 = DrugWithDoses(d2)
        ds1.doses = listOf(Dose(d2, now.minus(Duration.ofMinutes(12))))
        ds1.refreshData()

        val ds = repo.getNextUnavailableDrugToBecomeAvailable(listOf(ds1, ds2))
        assertNull(ds)
    }

    @Test
    fun testNextAvailableWithAllButOneDosesAreInThePast() {

        val now = Instant.now()

        val d1 = Drug("D1", 2, 10)
        val d2 = Drug("D2", 2, 10)

        val ds1 = DrugWithDoses(d1)
        ds1.doses = listOf(Dose(d1, now.minus(Duration.ofMinutes(5))))
        ds1.refreshData(now)

        val ds2 = DrugWithDoses(d2)
        ds2.doses = listOf(Dose(d2, now.minus(Duration.ofMinutes(12))))
        ds2.refreshData(now)

        val ds = repo.getNextUnavailableDrugToBecomeAvailable(listOf(ds1, ds2))
        assertEquals(ds1, ds)
    }

    @Test
    fun test2UnavailableDrugsGetsNearest() {

        val now = Instant.now()

        val d1 = Drug("D1", 2, 10)
        val d2 = Drug("D2", 2, 10)

        val ds1 = DrugWithDoses(d1)
        ds1.doses = listOf(Dose(d1, now.minus(Duration.ofMinutes(5))))
        ds1.refreshData(now)

        val ds2 = DrugWithDoses(d2)
        ds2.doses = listOf(Dose(d2, now.minus(Duration.ofMinutes(6))))
        ds2.refreshData(now)

        val ds = repo.getNextUnavailableDrugToBecomeAvailable(listOf(ds1, ds2))
        assertEquals(ds2, ds)

        assertEquals(ds2.secondsBeforeNextDoseAvailable, 4 * 60)
    }

    @Test
    fun insertingDrugThenDoseDaoDoesNotCauseKeyProblems() {

        var exceptionThrown = false
        val d = Drug("f1", 1, 1)

        try {
            runBlocking {
                repo.insertDrug(d)
                val dose = Dose(d, Instant.now())
                repo.insertDose(dose)
            }
        }catch(e: Exception){
            Log.d(TestRepo::javaClass.name, e.message)
            exceptionThrown = true
        }
        assertFalse("Inserting drug then dose did not throw exception", exceptionThrown)
    }


    @Test
    fun getAllLiveDrugsGetsAllDrugs() {

        runBlocking {
            repo.insertDrug(Drug("d"))
            repo.insertDrug(Drug("d2"))
        }

        val liveDrugs = repo.getAllDrugsLive().blockingObserve()

        assertEquals(4, liveDrugs?.size ?: 0) // There are 2 from the setup
    }


    @Test
    fun getAllDrugsWithDosesLiveGetsDoses() {

        val d = Dose(paracetamol)
        val j = launch { repo.insertDose(d) }

        runBlocking {
            j.join()
            val dd = repo.getAllDrugWithDosesLive().blockingObserve()
            val p = dd?.first { it.drug.id == paracetamol.id }
            assertEquals(1,p?.doses?.size ?: 0)
        }
    }

    @Test
    fun getDrugWithIdGetsCorrectDrug(){
        runBlocking {
            val d = repo.getDrugWithId(paracetamol.id)
            assertEquals(paracetamol, d)
        }
    }
}
