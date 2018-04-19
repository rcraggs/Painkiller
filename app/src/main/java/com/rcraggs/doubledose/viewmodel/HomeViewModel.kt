package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugStatus
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class HomeViewModel(private val repo: AppRepo): ViewModel() {

    private var internalStatusList: MutableList<DrugStatus> = repo.getDrugStatuses().toMutableList()
    private var drugStatusLiveData = MediatorLiveData<List<DrugStatus>>()

    init {
        drugStatusLiveData.addSource(repo.getAllDosesLive(), {
            repo.updateAllDrugStatuses(internalStatusList)
            drugStatusLiveData.value = internalStatusList.sortedBy { d -> d.drug.name }
        })

        drugStatusLiveData.addSource(repo.elapsedTime, {
            updateAllDrugStatusesAvailability()
            drugStatusLiveData.value = internalStatusList.sortedBy { d -> d.drug.name }
        })
    }

    fun getStatuses() = drugStatusLiveData

    fun getDrugs(): List<DrugStatus> {
        return internalStatusList.sortedBy { d -> d.drug.name }
    }

    private fun updateAllDrugStatusesAvailability() {

        for (ds in internalStatusList){
            ds.updateNextDoseAvailability()
        }
    }

    fun takeDose(drug: Drug) {
        launch(UI) {
            repo.insertDose(Dose(drug))
        }
        Log.d(this.javaClass.canonicalName, "Finished take dose thread")
    }

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {
        val drug = repo.findDrugById(drugId)
        val dose = Dose(drug)
        val takenTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
        dose.taken = takenTime.atZone(ZoneId.systemDefault()).toInstant()
        this.takeDose(drug)
    }

    private fun updateNotificationSchedule() {
        repo.rescheduleNotifications(internalStatusList)
    }
}
