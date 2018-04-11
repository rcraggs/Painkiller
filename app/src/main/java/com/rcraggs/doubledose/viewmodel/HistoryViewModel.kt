package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose

@Suppress("MemberVisibilityCanBePrivate")
class HistoryViewModel(val repo: AppRepo): ViewModel(){

    lateinit var doses: List<Dose>
    lateinit var drugName: String

    fun start(drugId: Long = -1L) {

        doses = when {
            drugId != -1L -> repo.getDosesWithDrugs(drugId)
            else -> repo.getDosesWithDrugs()
        }

        drugName = repo.getDrugWithId(drugId)?.name ?: "All Drugs"
    }
}