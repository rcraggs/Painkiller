package com.rcraggs.doubledose.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.SystemClock
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugStatus
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.util.dayAgo
import org.threeten.bp.Instant
import java.util.*


// todo make db private and all queries go through methods on the repo

class AppRepo(val db: AppDatabase) {

    /**
     * For a drug, create the status based on the doses that have been taken
     */
    fun getDrugStatus(drug: Drug): DrugStatus {

        val ds = DrugStatus(drug)
        refreshDrugStatus(ds)
        return ds
    }

    fun refreshDrugStatus(status: DrugStatus) {

        // get the doses that are relevant to updating the status of this drug
        val doses = db.doseDao().getDosesSince(status.drug.id, Instant.now().dayAgo())
        status.refreshData(doses)
    }
//
//    fun getDosesWithDrugs(): LiveData<List<Dose>> {
//
//        val doses = db.doseDao().getAllLive()
//        val drugs = db.drugDao().getAll()
//
//        doses.value?.forEach { d ->
//            d.drug = drugs.find { drug -> drug.id == d.drugId }!!
//        }
//
//        return doses
//    }
//
//    fun getDosesWithDrugs(drugId: Long): LiveData<List<Dose>> {
//
//        val drug = db.drugDao().findById(drugId)
//        val doses = db.doseDao().getAllLive(drugId)
//
//        doses.value?.forEach { d -> d.drug = drug }
//        return doses
//    }

    fun getDrugWithId(drugId: Long): Drug? = db.drugDao().findById(drugId)

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