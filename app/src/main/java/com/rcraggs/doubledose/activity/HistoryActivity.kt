package com.rcraggs.doubledose.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.ui.DoseAdapter
import com.rcraggs.doubledose.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.activity_history.*
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

class HistoryActivity : AppCompatActivity() {

    private val viewModel by viewModel<HistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        viewModel.start()

        rv_history.adapter = DoseAdapter(viewModel.doses)
        rv_history.layoutManager = LinearLayoutManager(this)
    }
}
