package com.rcraggs.doubledose.ui

import com.rcraggs.doubledose.model.DrugStatus

object UiUtilities {

    fun createDoseAvailableDesription(secondsBeforeNextDoseAvailable: Int): String {
        return if (secondsBeforeNextDoseAvailable == 0) {
            "Available!"
        } else {
            val minsTilAvail = secondsBeforeNextDoseAvailable.div(60)
            minsTilAvail.toString() + " mins"
        }
    }
}


fun List<DrugStatus>.getNextDrugToBecomeAvailable(): DrugStatus? {

    // Done ineligantly because filters seemed to not give the correct result each time
    val unavailableDrugs = this.filter { ds ->
        ds.secondsBeforeNextDoseAvailable > 0
    }

    val sortedUnavailable = unavailableDrugs.sortedBy {
        it.secondsBeforeNextDoseAvailable
    }

    return sortedUnavailable.firstOrNull()
}