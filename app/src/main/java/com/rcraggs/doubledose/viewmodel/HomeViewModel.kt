package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.ui.DrugStatus
import java.util.*


class HomeViewModel(context: Application): AndroidViewModel(context) {

    var setOfChangedDrugs = HashSet<Int>()

    private lateinit var drugs: List<DrugStatus>
    private lateinit var latestDose: LiveData<Dose>
    private val doseDao = AppDatabase.getInstance(context).doseDao()

    fun start() {
        latestDose = doseDao.getLatest()

        drugs = listOf(DrugStatus("Paracetamol", "PARACETAMOL"),
                DrugStatus("Ibroprufen", "IBROPRUFEN"))
    }

    fun getLatest() = latestDose

    fun getDrugs(): List<DrugStatus>? {
        return drugs
    }

    fun getChangesArray() = setOfChangedDrugs.toIntArray()

    fun clearChanges() {
        setOfChangedDrugs.clear()
    }

    fun takeDose(drugType: String) {

        doseDao.insert(Dose(drugType))

        // todo moved to a general purpose update all drugs thing?
        val updatedDrug: DrugStatus? = drugs.find { it.type == drugType }
        if (updatedDrug != null){
            updatedDrug.dosesIn24Hours++
            setOfChangedDrugs.add(drugs.indexOf(updatedDrug)) // So when we update the view we know which to update
        }
    }
}

