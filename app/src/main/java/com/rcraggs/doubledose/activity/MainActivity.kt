package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugAdapter
import com.rcraggs.doubledose.util.NotificationsService
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import org.threeten.bp.Instant
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<HomeViewModel>()
    private val notifications: NotificationsService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = DrugAdapter(viewModel.getDrugs(),
                viewModel::takeDose,
                this::chooseDoseTime,
                this::showDrugHistory
                )
        rv_drugs.adapter = adapter
        rv_drugs.layoutManager = LinearLayoutManager(this)

        viewModel.getStatuses().observe(this,
            Observer {
                adapter.notifyDataSetChanged()

                // If doses have changed and we've noticed in the UI, and they have not yet
                // been dealt with, update the notification scheduler.

                if (viewModel.requiresDoseScheduling()){
                    val nextAvailableDrug = viewModel.getDrugNextAvailableInFuture()
                    if (nextAvailableDrug != null) {
                        notifications.scheduleNotification(this, nextAvailableDrug)
                    }else{
                        notifications.cancelNotifications(this)
                    }

                    viewModel.doseReschedulingComplete() // flag this is has been dealt with
                }
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
            R.id.action_show_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {
        viewModel.takeDose(drugId, hourOfDay, minute)
    }
}
