package com.rcraggs.doubledose.ui

import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses
import com.rcraggs.doubledose.util.Constants
import kotlinx.android.synthetic.main.drug_card.view.*
import org.threeten.bp.*

object UiUtilities {

    fun createDoseAvailabilityDescription(drug: DrugWithDoses): String {

        return if (drug.secondsBeforeNextDoseAvailable == 0) {
            "Available!"
        } else {
            val minsTilAvail = createTextForMinutesToNextDose(drug.secondsBeforeNextDoseAvailable)
            "${createTextForNextDoseTime(drug.timeNextDoseIsAvailable)}   $minsTilAvail"
        }
    }

    fun createTextForMinutesToNextDose(seconds: Int): String {
        return if (seconds < 60){
            "< 1 min"
        }else{
            "${seconds.div(60)} mins"
        }
    }

    fun createTextForNextDoseTime(timeNextDoseIsAvailable: Instant?): String {

        val timeOfNextDose = LocalDateTime.ofInstant(timeNextDoseIsAvailable, ZoneId.systemDefault())
        var nextTimeString = Constants.doseTimeFormatter.format(timeOfNextDose)

        val todayMidnight =
                LocalDateTime.of(LocalDate.now(ZoneId.systemDefault()).plusDays(1), LocalTime.MIDNIGHT)

        if (timeOfNextDose.isAfter(todayMidnight)){
            nextTimeString = "Tmrw ${nextTimeString}"
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