package com.rcraggs.doubledose.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.SystemClock
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugStatus
import com.rcraggs.doubledose.ui.getNextDrugToBecomeAvailable
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.util.INotificationsService
import com.rcraggs.doubledose.util.dayAgo
import org.threeten.bp.Instant
import java.util.*

// todo make db private and all queries go through methods on the repo

class AppRepo(private val db: AppDatabase, private val notifications: INotificationsService) {

    /**
     * For a drug, create the status based on the doses that have been taken
     */
    private fun getDrugStatus(drug: Drug): DrugStatus {

        val ds = DrugStatus(drug)
        refreshDrugStatus(ds)
        return ds
    }

    private fun refreshDrugStatus(status: DrugStatus) {

        // get the doses that are relevant to updating the status of this drug
        val doses = db.doseDao().getDosesSince(status.drug.id, Instant.now().dayAgo())
        status.refreshData(doses)
    }

    fun getNextUnavailableDrugToBecomeAvailable(statuses: List<DrugStatus>? = null ): DrugStatus? {

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

    fun updateAllDrugStatuses(statuses: List<DrugStatus>) {
        for (ds in statuses){
            refreshDrugStatus(ds)
        }
    }

    fun getDrugStatuses(): List<DrugStatus> {

        val drugs = db.drugDao().getAll()
        val list = ArrayList<DrugStatus>()

        drugs.forEach {
            val status = getDrugStatus(it)
            list.add(status)
        }

        return list
    }

    fun rescheduleNotifications(list: List<DrugStatus>? = null) {
        val nextAvailableDrug = getNextUnavailableDrugToBecomeAvailable(list)
        if (nextAvailableDrug != null) {
            notifications.scheduleNotification(nextAvailableDrug.secondsBeforeNextDoseAvailable, nextAvailableDrug.drug.name)
        } else {
            notifications.cancelNotifications()
        }
    }

    fun getDrugWithId(drugId: Long): Drug? = db.drugDao().findById(drugId)

    fun getAllDosesLive() = db.doseDao().getAllLive()

    fun getAllDosesLive(drugId: Long) = db.doseDao().getAllLive(drugId)

    fun insertDose(dose: Dose) {
        db.doseDao().insert(dose)
        rescheduleNotifications()
    }

    fun findDrugById(drugId: Long): Drug {
        return db.drugDao().findById(drugId)
    }

    fun getAllDrugs() = db.drugDao().getAll()

    fun findDoseById(doseId: Long) = db.doseDao().findDoseById(doseId)

    fun deleteDose(id: Long) {
        db.doseDao().delete(id)
        rescheduleNotifications()
    }

    fun updateDose(dose: Dose) {
        db.doseDao().update(dose)
        rescheduleNotifications()
    }

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