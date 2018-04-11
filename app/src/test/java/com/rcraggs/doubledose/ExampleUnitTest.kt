package com.rcraggs.doubledose

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
class ExampleUnitTest {

    @Test
    fun testNoDosesMeansAvailable(){
        val status = DrugStatus(Drug("Ibroprufen"))
        status.updateNextDoseAvailability()
        assertEquals(Constants.NEXT_DOSE_AVAILABLE, status.getTimeUntilNextDose())
    }
}
