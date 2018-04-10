package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.ui.DrugStatus
import org.threeten.bp.Instant
import java.util.*


class HomeViewModel(repo: AppRepo): ViewModel() {

    var setOfChangedDrugs = HashSet<Int>()

    private lateinit var drugs: List<DrugStatus>
    private lateinit var latestDose: LiveData<Dose>
    private val doseDao = repo.db.doseDao()

    fun start() {
        latestDose = doseDao.getLatest()

        drugs = listOf(DrugStatus("Paracetamol"),
                DrugStatus("Ibroprufen"))

        drugs.forEachIndexed { index, _ ->
            updateDrugStatus(index)
        }
    }

    fun getUpdate() = latestDose

    fun getDrugs(): List<DrugStatus>? {
        return drugs
    }

    fun updateDrugStatus(pos: Int) {

        // todo is this something that a mediator can deal with?
        val drug = drugs[pos]
        drug.dosesIn24Hours = doseDao.getDosesSince(drug.type, Instant.now().minusSeconds(60 * 60 * 24)).size
        drug.timeOfLastDose = doseDao.getLatest(drug.type)?.taken
    }

    fun getChangesArray() = setOfChangedDrugs.toIntArray()

    fun clearChanges() {
        setOfChangedDrugs.clear()
    }

    fun takeDose(drugType: String) {

        doseDao.insert(Dose(drugType))

        val updatedDrug: DrugStatus? = drugs.find { it.type == drugType }
        if (updatedDrug != null){
            setOfChangedDrugs.add(drugs.indexOf(updatedDrug)) // So when we update the view we know which to update
        }
    }
}

