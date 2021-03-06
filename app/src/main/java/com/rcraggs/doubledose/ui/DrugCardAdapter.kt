package com.rcraggs.doubledose.ui

import android.arch.lifecycle.LiveData
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses
import com.rcraggs.doubledose.util.Constants
import kotlinx.android.synthetic.main.drug_card.view.*

class DrugCardAdapter(private val items: LiveData<List<DrugWithDoses>>,
                      private val doseAction: (Drug) -> Unit,
                      private val doseChooseAction: (Drug) -> Unit,
                      private val drugHistoryAction: (Drug) -> Unit
                  ): RecyclerView.Adapter<DrugCardAdapter.DrugHolder>() {

    override fun getItemCount() = items.value?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.drug_card, parent, false)
        return DrugHolder(v, doseAction, doseChooseAction, drugHistoryAction)
    }

    override fun onBindViewHolder(holder: DrugHolder, position: Int) {
        val item = items.value?.get(position)
        holder.bindDrug(item)
    }

    class DrugHolder(
            private val v: View,
            private val doseAction: (Drug) -> Unit,
            private val doseChooseAction: (Drug) -> Unit,
            private val drugHistoryAction: (Drug) -> Unit): RecyclerView.ViewHolder(v) {

        fun getDrugInfoView(): TextView = v.tv_next_dose_info
        fun getDoseNowView(): ImageView = v.img_dose_now
        fun getDoseLaterView(): ImageView = v.img_dose_choose
        fun getDoseHistory(): ImageView = v.img_drug_history
        fun getCard() = v

        fun bindDrug(item: DrugWithDoses?) {

            if (item == null){
                return
            }

            // Set up UI
            if (item.secondsBeforeNextDoseAvailable > 0){
                v.card_main.drug_card_banner.setBackgroundColor(Constants.UNAVAILABLE_DRUG_COLOR)
            }else{
                v.card_main.drug_card_banner.setBackgroundColor(Constants.AVAILABLE_DRUG_COLOR)
            }

            v.tv_medicine_type.text = item.drug.name
            v.tv_amount_taken.text = "${item.dosesIn24Hours}/${item.drug.dosesPerDay}"

            val doseAvailText = UiUtilities.createDoseAvailabilityDescription(item)
            v.tv_next_dose_info.text = doseAvailText

            // Event Handlers
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

