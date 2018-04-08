package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import com.rcraggs.doubledose.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders
                .of(this, ViewModelFactory(this.application))
                .get(HomeViewModel::class.java)

        viewModel.start()

        img_dose_now.setOnClickListener {
            viewModel.addDose()
        }

        viewModel.getUpdateTrigger().observe(this,
                Observer {
                    updateDetails()
                })

        updateDetails()
    }

    private fun updateDetails() {
        tv_amount_taken.text = viewModel.getTakenIn24House()
        tv_next_dose.text = viewModel.getTimeBeforeNextDose()
        tv_medicine_type.text = viewModel.getMedicineName()
    }
}
