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

// todo make db private and all queries go through methods on the repo

class AppRepo(private val db: AppDatabase, private val notifications: INotificationsService) {
//
//    /**
//     * For a drug, create the status based on the doses that have been taken
//     */
//    private suspend fun getDrugStatus(drug: Drug): DrugStatus {
//
//        val ds = DrugStatus(drug)
//        refreshDrugStatus(ds)
//        return ds
//    }
//
//    private suspend fun refreshDrugStatus(status: DrugStatus) {
//
//        // get the doses that are relevant to updating the status of this drug
//        val d = async(CommonPool){
//            db.doseDao().getDosesSince(status.drug.id, Instant.now().dayAgo())
//        }
//        status.refreshData(d.await())
//    }

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

//
//    suspend fun updateAllDrugStatuses(statuses: List<DrugStatus>) {
//
//        for (ds in statuses) {
//            refreshDrugStatus(ds)
//        }
//    }
//
//     fun getDrugStatusesLive(): LiveData<MutableList<DrugStatus>>? {
//
//        return Transformations.switchMap(db.drugDao().getAllLive()){
//            val list = MutableLiveData<MutableList<DrugStatus>>()
//            it.forEach {
//
//                list.value?.add(DrugStatus(it))
//            }
//            list
//        }
//    }

    fun getAllDrugsLive() = db.drugDao().getAllLive()

    fun getAllDrugWithDosesLive(): LiveData<List<DrugWithDoses>> {
        return db.drugDao().getAllDrugsWithDoses()
    }
//
//    private suspend fun getDrugStatuses(): List<DrugStatus> {
//
//        val d = async(CommonPool){
//            db.drugDao().getAll()
//        }
//
//        val list = ArrayList<DrugStatus>()
//
//        d.await().forEach {
//            val status = getDrugStatus(it)
//            list.add(status)
//        }
//
//        return list
//    }

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

    suspend fun insertDose(dose: Dose) {

        val job = launch(CommonPool) {
            db.doseDao().insert(dose)
            rescheduleNotifications()
        }

        job.join()
    }

    suspend fun findDrugById(drugId: Long): Drug {

        val d=  async(CommonPool) { db.drugDao().findById(drugId) }
        return d.await()
    }

    suspend fun getAllDrugs() = async(CommonPool){db.drugDao().getAll()}.await()

    suspend fun findDoseById(doseId: Long) = async(CommonPool) {db.doseDao().findDoseById(doseId)}.await()

    fun deleteDose(id: Long) {
        db.doseDao().delete(id)
        rescheduleNotifications()
    }

    fun updateDose(dose: Dose) {
        db.doseDao().update(dose)
        rescheduleNotifications()
    }
//
//    fun insertDrug(drug: Drug) {
//        launch(CommonPool) {
//            drug.id = db.drugDao().insert(drug)
//        }
//    }

    fun insertDrug(drug: Drug) {
        drug.id = db.drugDao().insert(drug)
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