package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.R.id.tv_no_history_message
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.ui.DoseAdapter
import com.rcraggs.doubledose.util.setUpVerticalFixedWidthWithRule
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
        setSupportActionBar(history_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Check if we have been started for a specific drug
        val drugId = intent.getLongExtra(HISTORY_ACTIVITY_EXTRA_DRUG_ID, -1)
        if (drugId > -1) {
            viewModel.start(drugId)
        }else{
            viewModel.start()
        }

        // if there are no doses then show the message, otherwise the list


        viewModel.doses.observe(this, Observer {
            if (rv_history.adapter == null){
                setUpRecyclerView(it)
            }else{
                adapter.items = it ?: ArrayList() // todo do a list diff or similar?

                if (it == null || it.isEmpty()){
                    setNoHistoryMessageVisible(true)
                }else {
                    rv_history.adapter.notifyDataSetChanged()
                }
            }
        })

        this.title = getString(R.string.activity_title_history) + ": " + viewModel.drugName
        rv_history.layoutManager = LinearLayoutManager(this)
    }

    private fun setUpRecyclerView(it: List<Dose>?) {

        if (it == null || it.isEmpty()) {
            setNoHistoryMessageVisible(true)
        }else {

            adapter = DoseAdapter(it, this::doseEditAction, this::doseDeleteAction).apply {
                setHasStableIds(true)
            }
            rv_history.adapter = adapter
            rv_history.setUpVerticalFixedWidthWithRule()
            setNoHistoryMessageVisible(false)
        }
    }

    private fun setNoHistoryMessageVisible(showMessage: Boolean) {
        if (showMessage){
            tv_no_history_message.visibility = View.VISIBLE
            rv_history.visibility = View.GONE
        }else {
            tv_no_history_message.visibility = View.GONE
            rv_history.visibility = View.VISIBLE
        }
    }

    private fun doseDeleteAction(it: Dose) {
        viewModel.deleteDose(it)
    }

    private fun doseEditAction(it: Dose) {
        startActivity(intentFor<DoseEditActivity>(
                DoseEditActivity.DOSE_EDIT_ACTIVITY_EXTRA_DOSE_ID to it.id))
    }
}
