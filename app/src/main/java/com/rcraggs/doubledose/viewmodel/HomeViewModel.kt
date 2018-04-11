package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugStatus
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*
import kotlin.collections.HashMap
import android.arch.lifecycle.MutableLiveData
import android.os.SystemClock
import com.rcraggs.doubledose.util.Constants


class HomeViewModel(private val repo: AppRepo): ViewModel() {

    private val doseDao = repo.db.doseDao()
    private val drugDao = repo.db.drugDao()

    private var setOfChangedDrugs = HashSet<Drug>()

    private lateinit var drugIdToStatusMap: Map<Long, DrugStatus>
    private lateinit var latestDose: LiveData<Dose>
    private val elapsedTime = MutableLiveData<Long>()

    fun start() {

        // Get the drugs and create statuses for them based on doses
        val drugs = drugDao.getAll()
        drugIdToStatusMap = HashMap()

        drugs.forEach {
            val status = repo.getDrugStatus(it)
            (drugIdToStatusMap as HashMap<Long, DrugStatus>)[it.id] = status
        }

        latestDose = doseDao.getLatest()


        // Set up the timer to refresh the data every 30 seconds
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                elapsedTime.postValue(SystemClock.elapsedRealtime())
            }
        }, Constants.REFRESH_TIMER_MILLI, Constants.REFRESH_TIMER_MILLI)
    }

    fun getUpdate() = latestDose

    fun getDrugs(): List<DrugStatus>? {
        return drugIdToStatusMap.values.sortedBy { d -> d.drug.name }
    }

    fun updateDrugStatus(drug: Drug) {
        drugIdToStatusMap[drug.id]?.let { repo.refreshDrugStatus(it) }
    }

    fun getChangesArray() = setOfChangedDrugs.toTypedArray()

    fun clearChanges() {
        setOfChangedDrugs.clear()
    }

    fun takeDose(drug: Drug) {

        doseDao.insert(Dose(drug))
        setDrugTypeAsChanged(drug)
    }

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {

        val drug = drugDao.findById(drugId)
        val dose = Dose(drug)
        val takenTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
        dose.taken = takenTime.atZone(ZoneId.systemDefault()).toInstant()
        doseDao.insert(dose)
        setDrugTypeAsChanged(drug)
    }

    private fun setDrugTypeAsChanged(drug: Drug) {
        setOfChangedDrugs.add(drug)
    }

    fun getTimer() = elapsedTime

    fun updateNextDoseStatusOfAll() {
        true
    }
}
