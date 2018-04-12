package com.rcraggs.doubledose

import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugStatus
import com.rcraggs.doubledose.util.Constants
import org.junit.Assert.assertEquals
import org.junit.Test
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
        status.updateNextDoseAvailability()
        assertEquals(Constants.NEXT_DOSE_AVAILABLE, status.getTimeUntilNextDose())
    }

    @Test
    fun testRefreshDrugWithNoDoses() {
        val status = DrugStatus(Drug("Ibroprufen"))
        val doses = ArrayList<Dose>()

        status.refreshData(doses)

        assertEquals(null, status.timeOfLastDose)
        assertEquals(0, status._minutesToNextDose)
    }
}
