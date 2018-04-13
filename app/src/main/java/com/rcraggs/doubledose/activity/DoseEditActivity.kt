package com.rcraggs.doubledose.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.viewmodel.DoseEditViewModel
import kotlinx.android.synthetic.main.activity_dose_edit.*
import org.koin.android.architecture.ext.viewModel

class DoseEditActivity : AppCompatActivity() {

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

        btn_delete_dose.setOnClickListener { deleteDose() }
    }

    private fun deleteDose() {
        viewModel.deleteDose(doseId)
        finish()
    }
}
