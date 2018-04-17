package com.rcraggs.doubledose.model

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.max


class DrugStatus(val drug: Drug) {

    private var dosesDescription: String = ""

    var timeOfLastDose: Instant? = null
    private var timeOfDoseThatIsMax: Instant? = null // if we can have 4 doses per day, when is the 4th most recent dose
    private var dosesIn24Hours = 0
    var secondsBeforeNextDoseAvailable = 0
    var timeNextDoseIsAvailable: Instant? = null

    private fun returnSecondsToNextDose(currentTime: Instant): Int {

        // No doses, no minutes until a next dose
        if (timeOfLastDose == null){
            return 0
        }

        // If there are more doses in 24 hours then they can't dose until the time of the
        // last relevant dose
        var secMaxDosesClear: Long = 0
        if (dosesIn24Hours >= drug.dosesPerDay) {

            val durationSinceMaxDose = Duration.between(timeOfDoseThatIsMax, currentTime)
            val howFarShortOf24HoursIsMaxDose = Duration.ofHours(24) - durationSinceMaxDose
            secMaxDosesClear = howFarShortOf24HoursIsMaxDose.seconds
        }

        val secSinceLastDose = Duration.between(timeOfLastDose, currentTime).seconds
        val secLastDoseClear = max(0, drug.gapMinutes * 60 - secSinceLastDose)

        return max(secLastDoseClear, secMaxDosesClear).toInt()
    }

    fun updateNextDoseAvailability(currentTime: Instant = Instant.now()) {

        secondsBeforeNextDoseAvailable = returnSecondsToNextDose(currentTime)
        timeNextDoseIsAvailable = currentTime.plusSeconds(secondsBeforeNextDoseAvailable.toLong())
    }

    fun refreshData(doses: List<Dose>, currentTime: Instant = Instant.now()) {

        dosesIn24Hours = doses.size

        // If there are doses then recall the first and last
        if (!doses.isEmpty()) {

            val dosesSortedByDate = doses.sortedBy { d -> d.taken }
            timeOfLastDose = dosesSortedByDate.last().taken

            if (dosesIn24Hours >= drug.dosesPerDay) {
                timeOfDoseThatIsMax = dosesSortedByDate[drug.dosesPerDay.toInt() - 1].taken
            }
        }
        dosesDescription = "$dosesIn24Hours/${drug.dosesPerDay}"
        updateNextDoseAvailability(currentTime)
    }

    fun getNumberOfDosesInfo() = dosesDescription //todo this should be two properties

    companion object {
        val doseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    }
}