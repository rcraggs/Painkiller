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

class HomeViewModel(private val repo: AppRepo): ViewModel() {

    private val doseDao = repo.db.doseDao()
    private val drugDao = repo.db.drugDao()

    private var drugIdToStatusMap: MutableList<DrugStatus>
    private var drugStatuses = MediatorLiveData<List<DrugStatus>>()

    private var requireNotificationUpdate = false

    init {
        // Get the drugs and create statuses for them based on doses
        val drugs = drugDao.getAll()
        drugIdToStatusMap = ArrayList()

        drugs.forEach {
            val status = repo.getDrugStatus(it)
            drugIdToStatusMap.add(status)
        }

        drugStatuses.addSource(doseDao.getAllLive(), {
            Log.d("HomeViewModel", "Refreshing Live Database BC table changed")
            updateAllDrugStatuses()
            drugStatuses.value = drugIdToStatusMap.sortedBy { d -> d.drug.name }
        })

        drugStatuses.addSource(repo.elapsedTime, {
            updateAllDrugStatusesAvailability()
            drugStatuses.value = drugIdToStatusMap.sortedBy { d -> d.drug.name }
            Log.d("HomeViewModel", "Refreshing Live Database BC timer tick")
        })

        requireNotificationUpdate = true
    }

    fun getStatuses() = drugStatuses

    fun getDrugs(): List<DrugStatus> {
        return drugIdToStatusMap.sortedBy { d -> d.drug.name }
    }

    private fun updateAllDrugStatusesAvailability() {

        for (ds in drugIdToStatusMap){
            ds.updateNextDoseAvailability()
        }
    }

    private fun updateAllDrugStatuses() {

        for (ds in drugIdToStatusMap){
            repo.refreshDrugStatus(ds)
        }
    }

    fun takeDose(drug: Drug) {
        doseDao.insert(Dose(drug))
        requireNotificationUpdate = true
    }

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {

        val drug = drugDao.findById(drugId)
        val dose = Dose(drug)
        val takenTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
        dose.taken = takenTime.atZone(ZoneId.systemDefault()).toInstant()
        doseDao.insert(dose)

        requireNotificationUpdate = true
    }

    fun getDrugNextAvailableInFuture(): DrugStatus? {

        var minutesToNextAvailableDose = Int.MAX_VALUE
        var nextAvailableDrugStatus: DrugStatus? = null

        drugStatuses.value?.forEach {
            if (it.minutesToNextDose > 0 && it.minutesToNextDose < minutesToNextAvailableDose){
                nextAvailableDrugStatus = it
                minutesToNextAvailableDose = it.minutesToNextDose
            }
        }

        return nextAvailableDrugStatus
    }

    fun requiresDoseScheduling() = requireNotificationUpdate
    fun doseReschedulingComplete() {
        requireNotificationUpdate = false
    }
}
