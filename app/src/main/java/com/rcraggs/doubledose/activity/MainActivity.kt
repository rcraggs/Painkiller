package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.ui.DrugAdapter
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import com.rcraggs.doubledose.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: DrugAdapter // todo - does this need to be a member?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders
                .of(this, ViewModelFactory(this.application))
                .get(HomeViewModel::class.java)

        viewModel.start()

        adapter = DrugAdapter(viewModel.getDrugs() ?: ArrayList(), viewModel::takeDose)
        rv_drugs.adapter = adapter
        rv_drugs.layoutManager = LinearLayoutManager(this)

        viewModel.getUpdate().observe(this,
                Observer {
                    viewModel.getChangesArray().forEach {
                        viewModel.updateDrugStatus(it)
                        adapter.notifyItemChanged(it)
                    }
                    viewModel.clearChanges()
                })
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
                Log.d("MainActvity", "History Clicked")
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
