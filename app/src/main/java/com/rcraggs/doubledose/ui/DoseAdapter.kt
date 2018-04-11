package com.rcraggs.doubledose.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import kotlinx.android.synthetic.main.dose_history_item.view.*
import kotlinx.android.synthetic.main.drug_card.view.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class DoseAdapter(private val items: List<Dose>): RecyclerView.Adapter<DoseAdapter.DoseHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoseHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.dose_history_item, parent, false)
        return DoseHolder(v)
    }

    override fun onBindViewHolder(holder: DoseHolder, position: Int) {
        val item = items[position]
        holder.bindDose(item)
    }

    class DoseHolder(private val v: View): RecyclerView.ViewHolder(v) {

        fun bindDose(item: Dose) {
            val ld1: LocalDateTime = LocalDateTime.ofInstant(item.taken, ZoneId.systemDefault())
            val takenTime = DrugStatus.doseTimeFormatter.format(ld1)
            v.tv_dose_history_text.text = "${item.drug.name} @ $takenTime"
        }
    }
}

