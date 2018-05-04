package com.rcraggs.doubledose.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.viewmodel.DrugAdminViewModel
import kotlinx.android.synthetic.main.activity_drug_add_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.yesButton
import org.koin.android.architecture.ext.viewModel

class DrugAddEditActivity : AppCompatActivity() {

    private val viewModel by viewModel<DrugAdminViewModel>()

    companion object {
        const val EXTRA_DRUG_ID  = "EXTRA_DRUG_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drug_add_edit)
        setSupportActionBar(drug_edit_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // If a drug was passed in, show the values
        if (intent.hasExtra(EXTRA_DRUG_ID)){
            viewModel.setDrugId(intent.getLongExtra(EXTRA_DRUG_ID, -1))
            ed_drug_name.setText(viewModel.drug.name)
            ed_dose_24.setText(viewModel.drug.dosesPerDay.toString())
            ed_gap.setText(viewModel.drug.gapMinutes.toString())
            sw_drug_enabled.isChecked = viewModel.drug.active
            btn_delete_drug.visibility = View.VISIBLE
            btn_update_drug.visibility = View.VISIBLE
            btn_add_drug.visibility = View.GONE
        }else{
            btn_delete_drug.visibility = View.GONE
            btn_update_drug.visibility = View.GONE
            btn_add_drug.visibility = View.VISIBLE
        }

        btn_add_drug.onClick {

            if (validateForm()) {
                viewModel.addDrug(
                        ed_drug_name.text.toString(),
                        ed_dose_24.text.toString(),
                        ed_gap.text.toString(),
                        sw_drug_enabled.isChecked)
                finish()
            }
        }

        btn_update_drug.onClick {

            if (validateForm()) {
                viewModel.updateDrug(
                        ed_drug_name.text.toString(),
                        ed_dose_24.text.toString(),
                        ed_gap.text.toString(),
                        sw_drug_enabled.isChecked)
                finish()
            }
        }

        btn_delete_drug.onClick {

            alert("Are you sure you want to delete ${viewModel.drug.name}?", "Delete") {
                yesButton {
                    viewModel.deleteDrug()
                    finish()
                }
                noButton {}
            }.show()
        }
    }

    private fun validateForm(): Boolean {

        til_p24.isErrorEnabled = false
        til_drug_name.isErrorEnabled = false
        til_gap.isErrorEnabled = false

        var validationOk = true

        if (ed_dose_24.text.isBlank()){
            til_p24.error = resources.getString(R.string.error_msg_requires_number)
            til_p24.isErrorEnabled = true
            validationOk = false

        }

        if (ed_gap.text.isBlank()){
            til_gap.error = resources.getString(R.string.error_msg_requires_number)
            til_gap.isErrorEnabled = true
            validationOk = false
        }

        if (ed_drug_name.text.isBlank()){
            til_drug_name.error = resources.getString(R.string.error_msg_requires_string)
            til_drug_name.isErrorEnabled = true
            validationOk = false
        }

        return validationOk
    }
}
