package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Drug

class DoseEditViewModel(val repo: AppRepo): ViewModel(){

    private var drugList: List<Drug> = repo.db.drugDao().getAll()
    fun getDrugs() = drugList

    fun deleteDose(doseId: Long) {
        repo.db.doseDao().delete(doseId)
    }
}