package com.rcraggs.doubledose.ui

import com.rcraggs.doubledose.model.Drug
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.MonthDay
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter


class DrugStatus(val type: String ) {

    var dosesIn24Hours: Int = 0
    var timeOfLastDose: Instant? = null

    fun getNumberOfDosesInfo() = "$dosesIn24Hours/${Drug.getInstance().maxDosesPer24Hours}"

    fun getTimeOfLastDoseInfo(): String {

        // if there is no last dose then display a message
        if (timeOfLastDose == null){
            return "No recent doses"
        }

        // Convert to a local time so we can compare
        val doseLocalDateTime = LocalDateTime.ofInstant(timeOfLastDose, ZoneId.systemDefault())
        val nowDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())

        // First check if the last dose was today so we don't need to print the date
        return if (nowDateTime.dayOfYear == doseLocalDateTime.dayOfYear &&
                nowDateTime.year == doseLocalDateTime.year){
            DateTimeFormatter.ISO_LOCAL_TIME.format(doseLocalDateTime)
        }else{
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(doseLocalDateTime)
        }
    }
}