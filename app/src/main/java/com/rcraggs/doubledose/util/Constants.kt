package com.rcraggs.doubledose.util

import android.graphics.Color
import org.threeten.bp.format.DateTimeFormatter

object Constants {

    const val NEXT_DOSE_AVAILABLE = "Available!"
    const val REFRESH_TIMER_MILLI = 1000L * 10
    const val CHANNEL_ID = "7298"

    const val PROD_DB_NAME = "dosesdb"

    val AVAILABLE_DRUG_TEXT_COLOR = Color.parseColor("#000000")
    val UNAVAILABLE_DRUG_TEXT_COLOR = Color.parseColor("#FFFFFF")
    val AVAILABLE_DRUG_COLOR = Color.parseColor("#D54200")
    val UNAVAILABLE_DRUG_COLOR = Color.parseColor("#9D9C9C")

    val doseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val historyDoseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E d-MMM-yyyy h:mm a")
    val doseDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E d-MMM-yyyy a")
}