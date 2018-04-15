package com.rcraggs.doubledose.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.viewmodel.DoseEditViewModel
import kotlinx.android.synthetic.main.activity_dose_edit.*
import org.koin.android.architecture.ext.viewModel

class DoseEditActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        const val DOSE_EDIT_ACTIVITY_EXTRA_DOSE_ID = "DOSE_EDIT_ACTIVITY_EXTRA_DOSE_ID"
    }

    private val viewModel by viewModel<DoseEditViewModel>()
    private var doseId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dose_edit)

        doseId = intent.getLongExtra(DOSE_EDIT_ACTIVITY_EXTRA_DOSE_ID, -1)

        // todo if a bad dose id is passed in then error message

        val spinnerAdapter = ArrayAdapter<Drug>(this, android.R.layout.simple_spinner_item, viewModel.getDrugs())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_drug.adapter = spinnerAdapter

        viewModel.setDose(doseId)

        time_dose_time.currentHour = viewModel.getDoseHour()
        time_dose_time.currentMinute = viewModel.getDoseMinutes()
        tv_current_date.text = viewModel.getDoseTimeString()
        sp_drug.setSelection(viewModel.getPositionOfDrugInList(), false)

        btn_delete_dose.setOnClickListener { deleteDose() }
        btn_update_dose.setOnClickListener { updateDose() }
        btn_change_date.setOnClickListener { updateDate() }

        time_dose_time.setOnTimeChangedListener { view, hourOfDay, minute ->
            viewModel.setNewTime(hourOfDay, minute)
        }

        sp_drug.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setNewDrug(sp_drug.selectedItem as Drug)
    }

    private fun deleteDose() {
        viewModel.deleteDose(doseId)
        finish()
    }

    private fun updateDose() {
        viewModel.updateDose()
        finish()
    }

    private fun updateDate() {

        val newFragment = DatePickerFragment()

        val bundle = Bundle()
        bundle.putLong(DatePickerFragment.ARG_DATE, viewModel.getDoseDateSeconds())
        newFragment.arguments = bundle

        newFragment.show(fragmentManager, "timepicker")
    }

    fun setNewDate(year: Int, month: Int, day: Int) {
        // Update the underlying model
        viewModel.setNewDate(year, month, day)
        tv_current_date.text = viewModel.getUpdatedDoseDate()
    }
}
