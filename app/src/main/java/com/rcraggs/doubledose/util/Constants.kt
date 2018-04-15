package com.rcraggs.doubledose.util

import android.graphics.Color
import org.threeten.bp.format.DateTimeFormatter

object Constants {

    const val NEXT_DOSE_AVAILABLE = "Available!"
    const val REFRESH_TIMER_MILLI = 1000L //* 30
    const val NEXT_DOSE_TIME_UNIT = " mins"
    const val SECONDS_IN_A_DAY = 24 * 60 * 60

    const val PROD_DB_NAME = "dosesdb"

    val AVAILABLE_DRUG_COLOR = Color.parseColor("#D2AA3C")
    val UNAVAILABLE_DRUG_COLOR = Color.parseColor("#A03555")

    val historyDoseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E d-MMM-yyyy h:mm a")
    val doseDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E d-MMM-yyyy a")
}