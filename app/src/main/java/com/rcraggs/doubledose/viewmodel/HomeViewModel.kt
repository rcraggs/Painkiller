package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import com.rcraggs.doubledose.database.AppDatabase
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Medicine

class HomeViewModel(val context: Application): AndroidViewModel(context) {

    private val update = MutableLiveData<Boolean>()
    fun getUpdateTrigger(): LiveData<Boolean> = update

    private var takenIn24Hours = "0/4"
    private var timeBeforeNextDose = "Take Some Now!"

    fun getTakenIn24House() = takenIn24Hours
    fun getTimeBeforeNextDose() = timeBeforeNextDose
    fun getMedicineName(): CharSequence? = "Paracetamol".toUpperCase()

    val doseDao = AppDatabase.getInstance(context).doseDao()

    fun start() {
        val liveData = MediatorLiveData<Boolean>()
        liveData.addSource(doseDao.getLatest(Medicine.PARACETAMOL), Observer {
            takenIn24Hours = "1/4"
            timeBeforeNextDose = "2 hours..."
            update.value = update.value ?: true
        })
    }

    fun addDose() {
        doseDao.insert(Dose(Medicine.PARACETAMOL))
    }
}

