package com.rcraggs.doubledose

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.database.DoseDao
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.util.blockingObserve
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

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
    }

    @After
    fun closeDb(){
        db.close()
    }

    @Test
    fun canSaveAndGetLatest() {

        val d = Dose("PARACETAMOL")
        doseDao.insert(d)
        val retrieved = doseDao.getLatest("PARACETAMOL").blockingObserve()
        assertEquals(retrieved, d)
    }


    @Test
    fun canSaveAndGet3() {

        doseDao.insert(Dose("PARACETAMOL"))
        doseDao.insert(Dose("PARACETAMOL"))
        doseDao.insert(Dose("IBROPRUFEN"))

        val retrieved = doseDao.getAll()
        assertEquals(retrieved.size, 3)
    }
}
