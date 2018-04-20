package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugStatus
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.custom.async
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class HomeViewModel(private val repo: AppRepo): ViewModel() {

    private lateinit var internalStatusList: MutableList<DrugStatus>
    private var drugStatusLiveData = MediatorLiveData<List<DrugStatus>>()

    fun start() {

        val d = async(CommonPool) { repo.getDrugStatuses() }

        runBlocking {
            internalStatusList = d.await().toMutableList()
        }


        drugStatusLiveData.addSource(repo.getAllDosesLive(), {
            launch(CommonPool) {
                repo.updateAllDrugStatuses(internalStatusList)
                drugStatusLiveData.postValue(internalStatusList.sortedBy { d -> d.drug.name }) // todo postvalue?
            }
        })

        drugStatusLiveData.addSource(repo.elapsedTime, {
            launch(CommonPool) {
                updateAllDrugStatusesAvailability()
                drugStatusLiveData.postValue(internalStatusList.sortedBy { d -> d.drug.name })
            }
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

        launch(UI) {
            val drug = repo.findDrugById(drugId)
            val dose = Dose(drug)
            val takenTime = LocalDateTime.now().withHour(hourOfDay).withMinute(minute)
            dose.taken = takenTime.atZone(ZoneId.systemDefault()).toInstant()
            takeDose(drug)
        }
    }
}
