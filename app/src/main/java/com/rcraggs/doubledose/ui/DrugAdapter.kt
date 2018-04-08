package com.rcraggs.doubledose.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import com.rcraggs.doubledose.viewmodel.objects.DrugStatus
import kotlinx.android.synthetic.main.drug_card.view.*
import java.util.*

class DrugAdapter(private val items: List<DrugStatus>, val vm: HomeViewModel): RecyclerView.Adapter<DrugHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.drug_card, parent, false)
        return DrugHolder(v, vm)
    }

    override fun onBindViewHolder(holder: DrugHolder, position: Int) {
        val item = items[position]
        holder.bindDrug(item)
    }
}

class DrugHolder(private val v: View, val vm: HomeViewModel): RecyclerView.ViewHolder(v) {

    // todo homeviewmodel as an interface with a click handler (or lambda)

    fun bindDrug(item: DrugStatus) {
        v.tv_medicine_type.text = item.name
        v.tv_amount_taken.text = "1/4"
        v.tv_next_dose.text = System.currentTimeMillis().toString()

        v.img_dose_now.setOnClickListener {
            vm.takeDose(item.type)
        }
    }
}