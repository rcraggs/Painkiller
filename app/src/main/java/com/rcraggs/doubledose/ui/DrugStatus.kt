package com.rcraggs.doubledose.ui

import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.util.Constants
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter


class DrugStatus(val drug: Drug) {

    // New Properties

    private var dosesDescription: String = ""
    private var nextDoseAvailability: String = ""

    var timeOfLastDose: Instant? = null


    fun updateNextDoseAvailability() {

    }


    fun refreshData(doses: List<Dose>) {

    }


    var dosesIn24Hours: Int = 0

    fun getNumberOfDosesInfo() = "$dosesIn24Hours/${drug.dosesPerDay}"

    fun getTimeUnitNextDose(): String {

        // todo what if they have taken too many doses in 24 hours?

        // None taken
        if (timeOfLastDose == null){
            return Constants.NEXT_DOSE_AVAILABLE
        }

        // Taken more than 2 hours ago
        return if (timeOfLastDose
                        ?.plusSeconds(60*drug.gap)
                        ?.isAfter(Instant.now()) != false){
            val secondsSinceDose = Instant.now().epochSecond.minus(timeOfLastDose?.epochSecond ?: 0)
            val secondsToNextDose = drug.gap * 60 - secondsSinceDose
            "${secondsToNextDose.div(60)}m"
            // todo replace that with the amount of hours
        }
        else{
            Constants.NEXT_DOSE_AVAILABLE
        }
    }

    fun getTimeOfLastDoseInfo(): String {

        // if there is no last dose then display a message
        if (timeOfLastDose == null){
            return Constants.NO_RECENT_DOSES
        }

        // Convert to a local time so we can compare
        val doseLocalDateTime = LocalDateTime.ofInstant(timeOfLastDose, ZoneId.systemDefault())
        val nowDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())

        // First check if the last dose was today so we don't need to print the date
        return if (nowDateTime.dayOfYear == doseLocalDateTime.dayOfYear &&
                nowDateTime.year == doseLocalDateTime.year){
            doseTimeFormatter.format(doseLocalDateTime)
        }else{
            Constants.NONE_TODAY
        }
    }

    companion object {
        val doseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    }
}