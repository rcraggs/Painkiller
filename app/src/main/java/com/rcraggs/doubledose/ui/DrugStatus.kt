package com.rcraggs.doubledose.ui

import android.util.Log
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.util.Constants
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.max


class DrugStatus(val drug: Drug) {

    private var dosesDescription: String = ""
    private var nextDoseAvailability: String = ""

    var timeOfLastDose: Instant? = null
    private var timeOfFirstDoseInThis24Hours: Instant? = null
    var dosesIn24Hours = 0
    var minutesToNextDose = 0

    private fun returnMinutesToNextDose(currentTime: Instant): Int {

        // No doses, no minutes until a next dose
        if (timeOfLastDose == null){
            return 0
        }

        // If there are more doses in 24 hours then they can't dose until the time of the
        // last relevant dose
        val secSinceFirstDose = Duration.between(timeOfFirstDoseInThis24Hours, currentTime).seconds
        var secMaxDosesClear: Long = 0
        if (dosesIn24Hours >= drug.dosesPerDay) {
            secMaxDosesClear = Constants.SECONDS_IN_A_DAY - secSinceFirstDose
        }

        val secSinceLastDose = Duration.between(timeOfLastDose, currentTime).seconds
        val secLastDoseClear = max(0, drug.gap * 60 - secSinceLastDose)

        return max(secLastDoseClear, secMaxDosesClear).toInt().div(60)
    }

    fun updateNextDoseAvailability(currentTime: Instant = Instant.now()) {
        minutesToNextDose = returnMinutesToNextDose(currentTime)

        if (minutesToNextDose > 0){
            nextDoseAvailability = "${minutesToNextDose}${Constants.NEXT_DOSE_TIME_UNIT}"
        }else{
            nextDoseAvailability = Constants.NEXT_DOSE_AVAILABLE
        }
    }

    fun refreshData(doses: List<Dose>, currentTime: Instant = Instant.now()) {

        // If there are doses then recall the first and last
        if (!doses.isEmpty()) {
            val dosesSortedByDate = doses.sortedBy { d -> d.taken }
            timeOfLastDose = dosesSortedByDate.last().taken
            timeOfFirstDoseInThis24Hours = dosesSortedByDate.first().taken
        }

        dosesIn24Hours = doses.size
        dosesDescription = "$dosesIn24Hours/${drug.dosesPerDay}"
        updateNextDoseAvailability(currentTime)
    }

    fun getNumberOfDosesInfo() = dosesDescription

    fun getTimeUntilNextDose() = nextDoseAvailability

    companion object {
        val doseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    }
}