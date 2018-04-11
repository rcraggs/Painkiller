package com.rcraggs.doubledose.activity

import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    companion object {
        const val ARG_DRUG_TYPE = "ARG_DRUG_TYPE"
    }

    private var drugId: Long? = null

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        if (drugId != null) {
            (activity as MainActivity).takeDose(drugId!!, hourOfDay, minute)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        drugId = arguments.getLong(ARG_DRUG_TYPE)

        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity,this, hour, minute,false)
    }
}
