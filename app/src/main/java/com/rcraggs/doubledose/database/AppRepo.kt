package com.rcraggs.doubledose.database

import android.arch.lifecycle.MutableLiveData
import android.os.SystemClock
import android.util.Log
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugStatus
import com.rcraggs.doubledose.ui.getNextDrugToBecomeAvailable
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.util.INotificationsService
import com.rcraggs.doubledose.util.dayAgo
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.threeten.bp.Instant
import java.util.*

// todo make db private and all queries go through methods on the repo

class AppRepo(private val db: AppDatabase, private val notifications: INotificationsService) {

    /**
     * For a drug, create the status based on the doses that have been taken
     */
    private suspend fun getDrugStatus(drug: Drug): DrugStatus {

        val ds = DrugStatus(drug)
        refreshDrugStatus(ds)
        return ds
    }

    private suspend fun refreshDrugStatus(status: DrugStatus) {

        // get the doses that are relevant to updating the status of this drug
        val d = async(CommonPool){
            db.doseDao().getDosesSince(status.drug.id, Instant.now().dayAgo())
        }
        status.refreshData(d.await())
    }

    suspend fun getNextUnavailableDrugToBecomeAvailable(statuses: List<DrugStatus>? = null ): DrugStatus? {

        val statusList: List<DrugStatus>?

        // If none were passed in, fetch them
        if (statuses == null){
            statusList = getDrugStatuses()
            updateAllDrugStatuses(statusList)
        }else{
            statusList = statuses
        }

        return statusList.getNextDrugToBecomeAvailable()
    }

    suspend fun updateAllDrugStatuses(statuses: List<DrugStatus>) {

        for (ds in statuses) {
            refreshDrugStatus(ds)
        }
    }

    suspend fun getDrugStatuses(): List<DrugStatus> {

        val d = async(CommonPool){
            db.drugDao().getAll()
        }

        val list = ArrayList<DrugStatus>()

        d.await().forEach {
            val status = getDrugStatus(it)
            list.add(status)
        }

        return list
    }

    suspend fun rescheduleNotifications(list: List<DrugStatus>? = null) {
        val nextAvailableDrug = getNextUnavailableDrugToBecomeAvailable(list)
        if (nextAvailableDrug != null) {
            notifications.scheduleNotification(nextAvailableDrug.secondsBeforeNextDoseAvailable, nextAvailableDrug.drug.name)
        } else {
            notifications.cancelNotifications()
        }

        Log.d(this.javaClass.canonicalName, "Finished scheduling notifications")
    }

    suspend fun getDrugWithId(drugId: Long): Drug? = async { db.drugDao().findById(drugId) }.await()

    fun getAllDosesLive() = db.doseDao().getAllLive()

    fun getAllDosesLive(drugId: Long) = db.doseDao().getAllLive(drugId)

    suspend fun insertDose(dose: Dose) {

        Log.d(this.javaClass.canonicalName, "Added dose to DB")
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

    suspend fun deleteDose(id: Long) {
        db.doseDao().delete(id)
        rescheduleNotifications()
    }

    suspend fun updateDose(dose: Dose) {
        db.doseDao().update(dose)
        rescheduleNotifications()
    }

    fun insertDrug(drug: Drug) {
        launch(CommonPool) {
            drug.id = db.drugDao().insert(drug)
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