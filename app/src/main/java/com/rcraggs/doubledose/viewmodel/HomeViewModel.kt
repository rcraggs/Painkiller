package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.viewmodel.objects.DrugStatus
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Medicine


class HomeViewModel(context: Application): AndroidViewModel(context) {

    private lateinit var drugs: List<DrugStatus>
    lateinit var latestDose: LiveData<Dose>
    private val doseDao = AppDatabase.getInstance(context).doseDao()

    fun start() {
        latestDose = doseDao.getLatest()

        drugs = listOf(DrugStatus("Paracetamol", Medicine.PARACETAMOL),
                DrugStatus("Ibroprufen", Medicine.IBROPRUFEN))
    }

    fun getLatest() = latestDose

    fun getDrugs(): List<DrugStatus>? {
        return drugs
    }

    fun takeDose(drugType: Medicine) {

        doseDao.insert(Dose(drugType))
        Log.d("HomeViewModel", "Taking dose of $drugType")

        // todo moved to a general purpose update all drugs thing?
        val updatedDrug: DrugStatus? = drugs.find { it.type == drugType }
        if (updatedDrug != null){
            updatedDrug.dosesIn24Hours++
        }
    }
}

