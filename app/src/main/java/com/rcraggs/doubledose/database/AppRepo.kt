package com.rcraggs.doubledose.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.SystemClock
import android.util.Log
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses
import com.rcraggs.doubledose.ui.getNextDrugToBecomeAvailable
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.util.INotificationsService
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*

class AppRepo(private val db: AppDatabase, private val notifications: INotificationsService) {

    fun getNextUnavailableDrugToBecomeAvailable(drugs: List<DrugWithDoses>? = null ): DrugWithDoses? {

        val statusList: List<DrugWithDoses>?

        // If none were passed in, fetch them
        if (drugs == null){
            statusList = db.drugDao().getAllDrugsWithDosesSync()
            statusList.forEach { it.refreshData() }
        }else{
            statusList = drugs
        }

        return statusList.getNextDrugToBecomeAvailable()
    }

    fun getAllDrugsLive() = db.drugDao().getAllLive()

    fun getAllDrugWithDosesLive(): LiveData<List<DrugWithDoses>> {
        return db.drugDao().getAllDrugsWithDoses()
    }

    fun rescheduleNotifications(list: List<DrugWithDoses>? = null) {
        val nextAvailableDrug = getNextUnavailableDrugToBecomeAvailable(list)
        if (nextAvailableDrug != null) {
            notifications.scheduleNotification(nextAvailableDrug.secondsBeforeNextDoseAvailable, nextAvailableDrug.drug.name)
        } else {
            notifications.cancelNotifications()
        }
    }

    suspend fun getDrugWithId(drugId: Long): Drug? = async { db.drugDao().findById(drugId) }.await()

    fun getAllDosesLive() = db.doseDao().getAllLive()

    fun getAllDosesLive(drugId: Long) = db.doseDao().getAllLive(drugId)

    fun insertDose(dose: Dose) {

        launch(CommonPool) {
            db.doseDao().insert(dose)
            rescheduleNotifications()
        }
    }

    suspend fun findDrugById(drugId: Long): Drug {

        val d=  async(CommonPool) { db.drugDao().findById(drugId) }
        return d.await()
    }

    suspend fun getAllDrugs() = async(CommonPool){db.drugDao().getAll()}.await()

    suspend fun findDoseById(doseId: Long) = async(CommonPool) {db.doseDao().findDoseById(doseId)}.await()

    fun deleteDose(id: Long) {
        launch {
            db.doseDao().delete(id)
            rescheduleNotifications()
        }
    }

    fun updateDose(dose: Dose) {
        launch {
            db.doseDao().update(dose)
            rescheduleNotifications()
        }
    }

    fun insertDrug(drug: Drug) {

        launch(CommonPool) {
            drug.id = db.drugDao().insert(drug)
            rescheduleNotifications()
        }
    }

    fun deleteDrug(drug: Drug) {
        launch(CommonPool) {
            db.drugDao().delete(drug)
            rescheduleNotifications()
        }
    }

    fun updateDrug(drug: Drug) {
        launch {
            db.drugDao().update(drug)
            rescheduleNotifications()
        }
    }

    private var _elapsedTime: MutableLiveData<Long>? = null

    val elapsedTime : MutableLiveData<Long>
        get() {
            if (_elapsedTime == null) {
                _elapsedTime = MutableLiveData()

                // Set up the timer to refresh the time regularly
                val timer = Timer()
                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        _elapsedTime?.postValue(SystemClock.elapsedRealtime())
                    }
                }, Constants.REFRESH_TIMER_MILLI, Constants.REFRESH_TIMER_MILLI)
            }
            return _elapsedTime ?: throw AssertionError("Set to null by another thread")
        }
}