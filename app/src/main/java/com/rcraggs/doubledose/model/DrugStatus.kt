package com.rcraggs.doubledose.model

import com.rcraggs.doubledose.util.Constants
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.max


class DrugStatus(val drug: Drug) {

    private var dosesDescription: String = ""

    var timeOfLastDose: Instant? = null
    private var timeOfFirstDoseInThis24Hours: Instant? = null
    private var dosesIn24Hours = 0
    var secondsBeforeNextDoseAvailable = 0

    private fun returnSecondsToNextDose(currentTime: Instant): Int {

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

        return max(secLastDoseClear, secMaxDosesClear).toInt()
    }

    fun updateNextDoseAvailability(currentTime: Instant = Instant.now()) {

        secondsBeforeNextDoseAvailable = returnSecondsToNextDose(currentTime)
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

    companion object {
        val doseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    }
}