package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.util.Log
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class HomeViewModel(application: Application, private val repo: AppRepo): AndroidViewModel(application) {

    var timer: LiveData<Long> = MutableLiveData<Long>()
    lateinit var drugWithDoses: LiveData<List<DrugWithDoses>>

    fun start() {

        drugWithDoses = Transformations.map(repo.getAllDrugWithDosesLive()) {
            it.forEach { it.refreshData() }
            it
        }

        timer = Transformations.map(repo.elapsedTime, {
            drugWithDoses.value?.forEach {
                it.updateNextDoseAvailability()
            }
            it
        })
    }

    fun takeDose(drug: Drug) {
        launch(UI) {
            repo.insertDose(Dose(drug))
        }
        Log.d(this.javaClass.canonicalName, "Finished take dose thread")
    }

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {

        launch(UI) {
            val drug = repo.findDrugById(drugId)
            val dose = Dose(drug)
            val takenTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
            dose.taken = takenTime.atZone(ZoneId.systemDefault()).toInstant()
            takeDose(drug)
        }
    }
}
