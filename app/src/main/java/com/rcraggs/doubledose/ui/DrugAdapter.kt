package com.rcraggs.doubledose.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Drug
import kotlinx.android.synthetic.main.drug_card.view.*

class DrugAdapter(private val items: List<DrugStatus>,
                  private val doseAction: (Drug) -> Unit,
                  private val doseChooseAction: (Drug) -> Unit,
                  private val drugHistoryAction: (Drug) -> Unit
                  ): RecyclerView.Adapter<DrugAdapter.DrugHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.drug_card, parent, false)
        return DrugHolder(v, doseAction, doseChooseAction, drugHistoryAction)
    }

    override fun onBindViewHolder(holder: DrugHolder, position: Int) {
        val item = items[position]
        holder.bindDrug(item)
    }

    class DrugHolder(
            private val v: View,
            private val doseAction: (Drug) -> Unit,
            private val doseChooseAction: (Drug) -> Unit,
            private val drugHistoryAction: (Drug) -> Unit)
        : RecyclerView.ViewHolder(v) {

        fun bindDrug(item: DrugStatus) {
            v.tv_medicine_type.text = item.drug.name
            v.tv_amount_taken.text = item.getNumberOfDosesInfo()
            v.tv_next_dose.text = item.getTimeUnitNextDose()

            v.img_dose_now.setOnClickListener {
                doseAction(item.drug)
            }

            v.img_dose_choose.setOnClickListener {
                doseChooseAction(item.drug)
            }

            v.img_drug_history.setOnClickListener {
                drugHistoryAction(item.drug)
            }

        }
    }
}

