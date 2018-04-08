package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Medicine
import com.rcraggs.doubledose.viewmodel.objects.DrugStatus


class HomeViewModel(context: Application): AndroidViewModel(context) {

    var setOfChangedDrugs = HashSet<Int>()

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

    fun getChangesArray() = setOfChangedDrugs.toIntArray()

    fun clearChanges() {
        setOfChangedDrugs.clear()
    }

    fun takeDose(drugType: Medicine) {

        doseDao.insert(Dose(drugType))
        Log.d("HomeViewModel", "Taking dose of $drugType")

        // todo moved to a general purpose update all drugs thing?
        val updatedDrug: DrugStatus? = drugs.find { it.type == drugType }
        if (updatedDrug != null){
            updatedDrug.dosesIn24Hours++
            setOfChangedDrugs.add(drugs.indexOf(updatedDrug)) // So when we update the view we know which to update
        }
    }
}

