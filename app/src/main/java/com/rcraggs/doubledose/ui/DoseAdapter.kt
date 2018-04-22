package com.rcraggs.doubledose.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.util.Constants
import kotlinx.android.synthetic.main.dose_history_item.view.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class DoseAdapter(var items: List<Dose>, private val doseAction: (Dose) -> Unit): RecyclerView.Adapter<DoseAdapter.DoseHolder>() {

    override fun getItemCount() = items?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoseHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.dose_history_item, parent, false)
        return DoseHolder(v, doseAction)
    }

    override fun onBindViewHolder(holder: DoseHolder, position: Int) {

        items?.let {
            holder.bindDose(items[position])
        }
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }
    class DoseHolder(private val v: View, private val doseAction: (Dose) -> Unit): RecyclerView.ViewHolder(v) {

        fun bindDose(item: Dose) {
            val ld1: LocalDateTime = LocalDateTime.ofInstant(item.taken, ZoneId.systemDefault())
            val takenTime = Constants.historyDoseTimeFormatter.format(ld1)
            v.tv_dose_history_drug.text = "${item.drug.name}"
            v.tv_dose_history_date.text =  "$takenTime"

            v.img_edit_dose.setOnClickListener {
                doseAction(item)
            }
        }
    }
}

