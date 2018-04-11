package com.rcraggs.doubledose.util

import org.threeten.bp.format.DateTimeFormatter

object Constants {

    val NEXT_DOSE_AVAILABLE = "Available!"
    val NO_RECENT_DOSES = "No recent doses"
    val NONE_TODAY = "None Today"

    val historyDoseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E d-MMM-yyyy h:mm a")
}