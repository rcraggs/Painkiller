package com.rcraggs.doubledose.ui

import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses
import com.rcraggs.doubledose.util.Constants
import org.threeten.bp.*

object UiUtilities {

    fun createDoseAvailableDesription(secondsBeforeNextDoseAvailable: Int): String {
        return if (secondsBeforeNextDoseAvailable == 0) {
            "Available!"
        } else {
            val minsTilAvail = secondsBeforeNextDoseAvailable.div(60)
            minsTilAvail.toString() + " mins"
        }
    }

    fun createTextForNextDoseTime(timeNextDoseIsAvailable: Instant?): String {

        val timeOfNextDose = LocalDateTime.ofInstant(timeNextDoseIsAvailable, ZoneId.systemDefault())
        var nextTimeString = Constants.doseTimeFormatter.format(timeOfNextDose)

        val todayMidnight =
                LocalDateTime.of(LocalDate.now(ZoneId.systemDefault()).plusDays(1), LocalTime.MIDNIGHT)

        if (timeOfNextDose.isAfter(todayMidnight)){
            nextTimeString = "Tomorrow ${nextTimeString}"
        }

        return nextTimeString
    }
}

fun List<DrugWithDoses>.getNextDrugToBecomeAvailable(): DrugWithDoses? {

    // Done ineligantly because filters seemed to not give the correct result each time
    val unavailableDrugs = this.filter { ds ->
        ds.secondsBeforeNextDoseAvailable > 0
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