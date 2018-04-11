package com.rcraggs.doubledose.util

import org.threeten.bp.format.DateTimeFormatter

object Constants {

    val NEXT_DOSE_AVAILABLE = "Available!"
    val NO_RECENT_DOSES = "No recent doses"
    val NONE_TODAY = "None Today"
    val REFRESH_TIMER_MILLI = 1000L * 30
    val NEXT_DOSE_TIME_UNIT = " mins"

    val historyDoseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E d-MMM-yyyy h:mm a")
}