package com.rcraggs.doubledose.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.ui.DoseAdapter
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.activity_history.*
import org.jetbrains.anko.intentFor
import org.koin.android.architecture.ext.viewModel

class HistoryActivity : AppCompatActivity() {

    companion object {
        const val HISTORY_ACTIVITY_EXTRA_DRUG_ID = "HISTORY_ACTIVITY_EXTRA_DRUG_ID"
    }
    private val viewModel by viewModel<HistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        Log.d("HistoryActivity", "History on create")

        // Check if we have been started for a specific drug
        val drugId = intent.getLongExtra(HISTORY_ACTIVITY_EXTRA_DRUG_ID, -1)
        if (drugId > -1) {
            viewModel.start(drugId)
        }else{
            viewModel.start()
        }

        this.title = "History: " + viewModel.drugName
        rv_history.adapter = DoseAdapter(viewModel.doses, this::editDose )
        rv_history.layoutManager = LinearLayoutManager(this)
    }

    private fun editDose(dose: Dose) {
        startActivity(intentFor<DoseEditActivity>(DoseEditActivity.DOSE_EDIT_ACTIVITY_EXTRA_DOSE_ID to dose.id))
    }

    override fun onResume() {
        super.onResume()
        rv_history.adapter.notifyDataSetChanged()
        Log.d("HistoryActivity", "History on resume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("HistoryActivity", "History on restart")
    }

}
