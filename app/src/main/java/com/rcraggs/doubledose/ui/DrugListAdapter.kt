package com.rcraggs.doubledose.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.di.getContext
import com.rcraggs.doubledose.model.Drug
import kotlinx.android.synthetic.main.drug_card.view.*
import kotlinx.android.synthetic.main.drug_list_items.view.*

class DrugListAdapter(var items: List<Drug>, // todo val and utils
                      private val clickAction: (Drug) -> Unit
): RecyclerView.Adapter<DrugListAdapter.DrugHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.drug_list_items, parent, false)
        return DrugHolder(v, clickAction)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DrugHolder, position: Int) {
        holder.bindDrug(items[position])
    }

    class DrugHolder(
            private val v: View,
            private val clickAction: (Drug) -> Unit)
        : RecyclerView.ViewHolder(v) {

        fun bindDrug(item: Drug) {

            val drugSuffix = if (item.active) "" else " (disabled)"
            v.tv_drug_list_name.text =  "${item.name}$drugSuffix"

            v.img_edit_drug.setOnClickListener {
                clickAction(item)
            }
        }
    }
}