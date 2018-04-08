package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.viewmodel.objects.DrugStatus

class HomeViewModel(context: Application): AndroidViewModel(context) {

    private val doseDao = AppDatabase.getInstance(context).doseDao()

    fun start() {
    }

    fun getDrugStatuses(): List<DrugStatus>? {
        return listOf(DrugStatus("Paracetamol"), DrugStatus("Ibroprufen"))
    }
}

