package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
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
    private lateinit var adapter: DoseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Check if we have been started for a specific drug
        val drugId = intent.getLongExtra(HISTORY_ACTIVITY_EXTRA_DRUG_ID, -1)
        if (drugId > -1) {
            viewModel.start(drugId)
        }else{
            viewModel.start()
        }
        viewModel.doses.observe(this, Observer {

            if (rv_history.adapter == null){
                adapter = DoseAdapter(it ?: ArrayList(), this::editDose) // todo: Pass this straight in as lambda
                rv_history.adapter = adapter
            }else{
                adapter.items = it ?: ArrayList() // todo do a list diff or similar?
                rv_history.adapter.notifyDataSetChanged()
            }
        })

        this.title = "History: " + viewModel.drugName
        rv_history.layoutManager = LinearLayoutManager(this)
    }

    private fun editDose(dose: Dose) {
        startActivity(intentFor<DoseEditActivity>(DoseEditActivity.DOSE_EDIT_ACTIVITY_EXTRA_DOSE_ID to dose.id))
    }
}
