package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose

@Suppress("MemberVisibilityCanBePrivate")
class HistoryViewModel(val repo: AppRepo): ViewModel(){

    lateinit var doses: List<Dose>

    fun start(drugId: Long = -1L) {

        val doseDao = repo.db.doseDao()

        doses = if (drugId == -1L){
            repo.getDosesWithDrugs()
        }else{
            repo.getDosesWithDrugs(drugId)
        }
    }
}