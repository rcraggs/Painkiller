package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.ui.DrugListAdapter
import com.rcraggs.doubledose.viewmodel.DrugAdminViewModel
import kotlinx.android.synthetic.main.activity_drug_admin.*
import org.jetbrains.anko.intentFor
import org.koin.android.architecture.ext.viewModel

class DrugAdminActivity : AppCompatActivity() {

    private val viewModel by viewModel<DrugAdminViewModel>()
    private lateinit var adapter: DrugListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drug_admin)

        viewModel.drugs.observe(this, Observer {

            if (rv_dose_admin.adapter == null){

                adapter = DrugListAdapter(it ?: ArrayList()){
                    startActivity(intentFor<DoseEditActivity>(
                            DoseEditActivity.DOSE_EDIT_ACTIVITY_EXTRA_DOSE_ID to it.id))
                }.apply {
                    setHasStableIds(true)
                }

                rv_dose_admin.adapter = adapter
                rv_dose_admin.setHasFixedSize(true)

            }else{
                adapter.items = it ?: ArrayList() // todo do a list diff or similar?
                rv_dose_admin.adapter.notifyDataSetChanged()
            }
        })

        rv_dose_admin.layoutManager = LinearLayoutManager(this)

//        setSupportActionBar(toolbar)

        fab_add_drug.setOnClickListener { view ->
            Log.d(javaClass.name, "FAB Click Listener")
        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
