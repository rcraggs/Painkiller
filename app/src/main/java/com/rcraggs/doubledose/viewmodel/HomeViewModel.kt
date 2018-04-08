package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.viewmodel.objects.DrugStatus
import android.arch.lifecycle.MediatorLiveData
import android.util.Log
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Medicine


class HomeViewModel(context: Application): AndroidViewModel(context) {

    var liveDataMerger: MediatorLiveData<DrugStatus> = MediatorLiveData()



    private val doseDao = AppDatabase.getInstance(context).doseDao()

    fun start() {


    }




    fun getDrugStatuses(): List<DrugStatus>? {
        return listOf(DrugStatus("Paracetamol", Medicine.PARACETAMOL),
                DrugStatus("Ibroprufen", Medicine.IBROPRUFEN))
    }

    fun takeDose(drugType: Medicine) {
        doseDao.insert(Dose(drugType))
        Log.d("HomeViewModel", "Taking dose of $drugType")
    }
}

