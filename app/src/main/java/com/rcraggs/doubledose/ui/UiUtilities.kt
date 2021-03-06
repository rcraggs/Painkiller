package com.rcraggs.doubledose.ui

import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses
import com.rcraggs.doubledose.util.Constants
import org.threeten.bp.*

object UiUtilities {

    fun createDoseAvailabilityDescription(drug: DrugWithDoses): String {

        return if (drug.secondsBeforeNextDoseAvailable == 0L) {
            "Available!"
        } else {
            val minsTilAvail = createTextForMinutesToNextDose(drug.secondsBeforeNextDoseAvailable)
            "${createTextForNextDoseTime(drug.timeNextDoseIsAvailable)}   $minsTilAvail"
        }
    }

    private fun createTextForMinutesToNextDose(seconds: Long): String {
        return if (seconds < 60){
            "< 1 min"
        }else{
            "${seconds.div(60)} mins"
        }
    }

    private fun createTextForNextDoseTime(timeNextDoseIsAvailable: Instant?): String {

        val timeOfNextDose = LocalDateTime.ofInstant(timeNextDoseIsAvailable, ZoneId.systemDefault())
        var nextTimeString = Constants.doseTimeFormatter.format(timeOfNextDose)

        val todayMidnight =
                LocalDateTime.of(LocalDate.now(ZoneId.systemDefault()).plusDays(1), LocalTime.MIDNIGHT)

        if (timeOfNextDose.isAfter(todayMidnight)){
            nextTimeString = "Tmrw $nextTimeString"
        }

        return nextTimeString
    }
}

fun List<DrugWithDoses>.getNextDrugToBecomeAvailable(): DrugWithDoses? {

    // Done ineligantly because filters seemed to not give the correct result each time
    val unavailableDrugs = this.filter { ds ->
        ds.secondsBeforeNextDoseAvailable > 0 && ds.drug.active
    }

    val sortedUnavailable = unavailableDrugs.sortedBy {
        it.secondsBeforeNextDoseAvailable
    }

    return sortedUnavailable.firstOrNull()
}


fun Drug.createWithDoses(doses: List<Instant> = ArrayList(), time: Instant? = null):DrugWithDoses {

    val dd = DrugWithDoses(this)
    dd.doses = doses.map { Dose(this, it) }

    // If a time is passed in, refresh the DD
    if (time!=null){
        dd.refreshData(time)
    }

    return dd
}

fun getDoseDescription(): String {
    val takenTime = LocalDateTime.now()
    return Constants.historyDoseTimeFormatter.format(takenTime)
}

fun getDoseDescription(hourOfDay: Int, minute: Int): String {
    val takenTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
    return Constants.historyDoseTimeFormatter.format(takenTime)
}
