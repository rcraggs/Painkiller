package com.rcraggs.doubledose.ui

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.viewmodel.objects.DrugStatus
import kotlinx.android.synthetic.main.drug_card.view.*

class DrugAdapter(private val items: List<DrugStatus>): RecyclerView.Adapter<DrugHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.drug_card, parent, false)
        return DrugHolder(v)
    }

    override fun onBindViewHolder(holder: DrugHolder, position: Int) {
        val item = items[position]
        holder.bindDrug(item)
    }
}

class DrugHolder(private val v: View): RecyclerView.ViewHolder(v) {

    init {
        v.setOnClickListener {
            Log.d("RecyclerView", "CLICK!")
        }
    }

    fun bindDrug(item: DrugStatus) {
        v.tv_medicine_type.text = item.name
        v.tv_amount_taken.text = "1/4"
        v.tv_next_dose.text = "20 mins"

    }
}