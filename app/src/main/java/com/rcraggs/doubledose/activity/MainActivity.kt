package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugCardAdapter
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.start()

        val adapter = DrugCardAdapter(viewModel.getDrugs(),
                viewModel::takeDose,
                this::chooseDoseTime,
                this::showDrugHistory
                )
        rv_drugs.adapter = adapter
        rv_drugs.layoutManager = LinearLayoutManager(this)

        viewModel.getStatuses().observe(this,
            Observer {
                adapter.notifyDataSetChanged()
            })
    }

    private fun showDrugHistory(drug: Drug) {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra( HistoryActivity.HISTORY_ACTIVITY_EXTRA_DRUG_ID, drug.id)
        startActivity(intent)
    }

    private fun chooseDoseTime(drug: Drug) {
        val newFragment = TimePickerFragment()

        // Set the drug type
        val bundle = Bundle()
        bundle.putLong(TimePickerFragment.ARG_DRUG_TYPE, drug.id)
        newFragment.arguments = bundle

        newFragment.show(fragmentManager, "timepicker")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.action_settings -> {
                true
            }
            R.id.action_update_drugs -> {
                val intent = Intent(this, DrugAdminActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_show_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {
        viewModel.takeDose(drugId, hourOfDay, minute) // todo could this be behind an interface?
    }
}
