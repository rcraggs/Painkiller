package com.rcraggs.doubledose.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.ui.DoseAdapter
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import com.rcraggs.doubledose.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        viewModel = ViewModelProviders
                .of(this, ViewModelFactory(this.application))
                .get(HistoryViewModel::class.java)

        viewModel.start()

        rv_history.adapter = DoseAdapter(viewModel.doses)
        rv_history.layoutManager = LinearLayoutManager(this)
    }
}
