package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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



        viewModel.getLatest().observe(this,
                Observer {
                    Log.d("MainActivity", "Noticed Data Change in Main Activity")
                })
    }

    private fun updateDetails() {
//        tv_amount_taken.text = viewModel.getTakenIn24House()
//        tv_next_dose.text = viewModel.getTimeBeforeNextDose()
//        tv_medicine_type.text = viewModel.getMedicineName()
    }
}
