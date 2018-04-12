package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugStatus
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*
import kotlin.collections.HashMap

class HomeViewModel(private val repo: AppRepo): ViewModel() {

    private val doseDao = repo.db.doseDao()
    private val drugDao = repo.db.drugDao()

    private var setOfChangedDrugs = HashSet<Drug>()

    private lateinit var drugIdToStatusMap: Map<Long, DrugStatus>
    private var drugStatuses = MediatorLiveData<List<DrugStatus>>()

    fun start() {

        // Get the drugs and create statuses for them based on doses
        val drugs = drugDao.getAll()
        drugIdToStatusMap = HashMap()

        drugs.forEach {
            val status = repo.getDrugStatus(it)
            (drugIdToStatusMap as HashMap<Long, DrugStatus>)[it.id] = status
        }

        drugStatuses.addSource(doseDao.getAllLive(), {
            Log.d("HomeViewModel", "Refreshing Live Database BC table changed")
            updateAllDrugStatuses()
            drugStatuses.value = drugIdToStatusMap.values.toList().sortedBy { d -> d.drug.name }
        })

        drugStatuses.addSource(repo.elapsedTime, {
            updateAllDrugStatusesAvailability()
            drugStatuses.value = drugIdToStatusMap.values.toList().sortedBy { d -> d.drug.name }
            Log.d("HomeViewModel", "Refreshing Live Database BC timer tick")
        })
    }

    fun getStatuses() = drugStatuses

    fun getDrugs(): List<DrugStatus> {
        return drugIdToStatusMap.values.sortedBy { d -> d.drug.name }
    }

    private fun updateAllDrugStatusesAvailability() {
        drugIdToStatusMap.forEach { _, u -> u.updateNextDoseAvailability() }
    }

    private fun updateAllDrugStatuses() {
        drugIdToStatusMap.forEach { _, u -> repo.refreshDrugStatus(u)  }
    }

    fun takeDose(drug: Drug) {
        doseDao.insert(Dose(drug))
    }

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {

        val drug = drugDao.findById(drugId)
        val dose = Dose(drug)
        val takenTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
        dose.taken = takenTime.atZone(ZoneId.systemDefault()).toInstant()
        doseDao.insert(dose)
    }
}
