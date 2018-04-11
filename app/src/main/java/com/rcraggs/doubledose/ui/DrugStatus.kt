package com.rcraggs.doubledose.ui

import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.util.Constants
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter


class DrugStatus(val drug: Drug) {

    private var dosesDescription: String = ""
    private var nextDoseAvailability: String = ""

    var timeOfLastDose: Instant? = null
    var dosesIn24Hours = 0
    var _minutesToNextDose = 0

    private fun getMinutesToNextDose(): Int {

        // None taken
        if (timeOfLastDose == null){
            return 0
        }

        val timeNextDoseIsAllowed = timeOfLastDose?.plusSeconds(60*drug.gap)
                ?: Instant.now()

        // Taken more than 2 hours ago
        if (timeNextDoseIsAllowed.isAfter(Instant.now())){
            val secondsSinceDose = Instant.now().epochSecond.minus(timeOfLastDose?.epochSecond ?: 0)
            val secondsToNextDose = drug.gap * 60 - secondsSinceDose
            return secondsToNextDose.div(60).toInt()
        }
        else{
            return 0
        }
    }

    fun updateNextDoseAvailability() {
        _minutesToNextDose = getMinutesToNextDose()
        if (_minutesToNextDose > 0){
            nextDoseAvailability = "${_minutesToNextDose}${Constants.NEXT_DOSE_TIME_UNIT}"
        }else{
            nextDoseAvailability = Constants.NEXT_DOSE_AVAILABLE
        }
    }

    fun refreshData(doses: List<Dose>) {
        timeOfLastDose = doses.sortedBy { d -> d.taken }.last().taken
        dosesIn24Hours = doses.size
        dosesDescription = "$dosesIn24Hours/${drug.dosesPerDay}"
        updateNextDoseAvailability()
    }

    fun getNumberOfDosesInfo() = dosesDescription

    fun getTimeUntilNextDose() = nextDoseAvailability
//
//    fun getTimeOfLastDoseInfo(): String {
//
//        // if there is no last dose then display a message
//        if (timeOfLastDose == null){
//            return Constants.NO_RECENT_DOSES
//        }
//
//        // Convert to a local time so we can compare
//        val doseLocalDateTime = LocalDateTime.ofInstant(timeOfLastDose, ZoneId.systemDefault())
//        val nowDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
//
//        // First check if the last dose was today so we don't need to print the date
//        return if (nowDateTime.dayOfYear == doseLocalDateTime.dayOfYear &&
//                nowDateTime.year == doseLocalDateTime.year){
//            doseTimeFormatter.format(doseLocalDateTime)
//        }else{
//            Constants.NONE_TODAY
//        }
//    }

    companion object {
        val doseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    }
}