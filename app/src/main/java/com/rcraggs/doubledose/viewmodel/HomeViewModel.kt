package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugStatus
import com.rcraggs.doubledose.util.INotificationsService
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class HomeViewModel(private val repo: AppRepo, private val notifications: INotificationsService): ViewModel() {

    private val doseDao = repo.db.doseDao()
    private val drugDao = repo.db.drugDao()

    private var internalStatusList: MutableList<DrugStatus> = repo.getDrugStatuses().toMutableList()
    private var drugStatusLiveData = MediatorLiveData<List<DrugStatus>>()

    init {
        drugStatusLiveData.addSource(doseDao.getAllLive(), {
            Log.d("HomeViewModel", "Refreshing Live Database BC table changed")
            repo.updateAllDrugStatuses(internalStatusList)
            drugStatusLiveData.value = internalStatusList.sortedBy { d -> d.drug.name }
        })

        drugStatusLiveData.addSource(repo.elapsedTime, {
            updateAllDrugStatusesAvailability()
            drugStatusLiveData.value = internalStatusList.sortedBy { d -> d.drug.name }
            Log.d("HomeViewModel", "Refreshing Live Database BC timer tick")
        })

        updateNotificationSchedule()
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
        doseDao.insert(Dose(drug))
        updateNotificationSchedule()
    }

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {
        val drug = drugDao.findById(drugId)
        val dose = Dose(drug)
        val takenTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
        dose.taken = takenTime.atZone(ZoneId.systemDefault()).toInstant()
        this.takeDose(drug)
    }

    private fun updateNotificationSchedule() {
        repo.rescheduleNotifications(internalStatusList)
    }
}
