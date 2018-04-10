package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose

@Suppress("MemberVisibilityCanBePrivate")
class HistoryViewModel(val repo: AppRepo): ViewModel(){

    lateinit var doses: List<Dose>

    fun start(type: String = "") {

        val doseDao = repo.db.doseDao()

        doses = if (type.isBlank()){
            doseDao.getAll()
        }else{
            doseDao.getAll(type)
        }
    }
}