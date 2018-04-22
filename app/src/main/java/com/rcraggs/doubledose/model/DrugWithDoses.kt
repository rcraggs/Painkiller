package com.rcraggs.doubledose.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import com.rcraggs.doubledose.util.dayAgo
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import kotlin.math.max

class DrugWithDoses() {

    @Embedded
    var drug: Drug = Drug("")

    @Relation(
            parentColumn = "id",
            entityColumn = "drug",
            entity = Dose::class)
    var doses: List<Dose> = listOf()

    constructor(d: Drug) : this() {
        drug = d
    }

    @Ignore var timeOfLastDose: Instant? = null
    @Ignore private var timeOfDoseThatIsMax: Instant? = null // if we can have 4 doses per day, when is the 4th most recent dose
    @Ignore var dosesIn24Hours: Int = 0
    @Ignore var secondsBeforeNextDoseAvailable: Int = 0
    @Ignore var timeNextDoseIsAvailable: Instant? = null
    @Ignore lateinit var listOfDosesIn24HoursSinceRefresh: List<Dose>

    private fun returnSecondsToNextDose(currentTime: Instant): Int {

        // No doses, no minutes until a next dose
        if (timeOfLastDose == null){
            return 0
        }

        // If there are more doses in 24 hours then they can't dose until the time of the
        // last relevant dose
        var secMaxDosesClear = 0
        if (dosesIn24Hours >= drug.dosesPerDay) {

            val durationSinceMaxDose = Duration.between(timeOfDoseThatIsMax, currentTime)
            val howFarShortOf24HoursIsMaxDose = Duration.ofHours(24) - durationSinceMaxDose
            secMaxDosesClear = howFarShortOf24HoursIsMaxDose.seconds.toInt()
        }

        val secSinceLastDose = Duration.between(timeOfLastDose, currentTime).seconds.toInt()
        val secLastDoseClear = max(0, drug.gapMinutes * 60 - secSinceLastDose).toInt()

        return max(secLastDoseClear, secMaxDosesClear)
    }

    fun updateNextDoseAvailability(currentTime: Instant = Instant.now()) {

        // Recalculate how many doses we've had in the last 24 hours
        dosesIn24Hours = listOfDosesIn24HoursSinceRefresh?.count {
            it.taken > currentTime.minus(Duration.ofHours(24))
        }

        secondsBeforeNextDoseAvailable = returnSecondsToNextDose(currentTime)
        timeNextDoseIsAvailable = currentTime.plusSeconds(secondsBeforeNextDoseAvailable.toLong())
    }

    fun refreshData(currentTime: Instant = Instant.now()) {

        listOfDosesIn24HoursSinceRefresh = doses.filter { d ->
            d.taken >= Instant.now().dayAgo()
        }

        val dosesIn24Hours= listOfDosesIn24HoursSinceRefresh.size

        // If there are doses then recall the first and last
        if (!listOfDosesIn24HoursSinceRefresh.isEmpty()) {

            val dosesSortedByDate = listOfDosesIn24HoursSinceRefresh.sortedBy { d -> d.taken }
            timeOfLastDose = dosesSortedByDate.last().taken

            if (dosesIn24Hours >= drug.dosesPerDay) {
                // So we know when the max doses clears
                timeOfDoseThatIsMax = dosesSortedByDate[drug.dosesPerDay.toInt() - 1].taken
            }
        }

        updateNextDoseAvailability(currentTime)
    }
}
