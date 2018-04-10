package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Context
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.model.Dose

class HistoryViewModel(context: Context): ViewModel(){

    lateinit var doses: List<Dose>
    private val doseDao = AppDatabase.getInstance(context).doseDao()

    fun start(type: String = "") {

        doses = if (type == null || type.isBlank()){
            doseDao.getAll()
        }else{
            doseDao.getAll(type)
        }
    }

}