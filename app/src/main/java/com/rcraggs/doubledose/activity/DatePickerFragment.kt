package com.rcraggs.doubledose.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.DatePicker
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val ARG_DATE = "ARG_DATE"
    }

    lateinit var date: LocalDate

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val seconds = arguments.getLong(ARG_DATE)
        date = Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDate()
        return DatePickerDialog(activity, this, date.year, date.monthValue - 1, date.dayOfMonth)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        (activity as DoseEditActivity).setNewDate(year, month, day)
    }
}