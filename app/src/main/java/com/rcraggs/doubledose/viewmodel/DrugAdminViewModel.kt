package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Drug
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

class DrugAdminViewModel(val repo: AppRepo, application: Application) : AndroidViewModel(application) {

    /**
     * For drug list
     */

    val drugs = repo.getAllDrugsLive()



    /**
     * For Drug Edit
     */

    internal lateinit var drug: Drug
    private var isAdd: Boolean = true

    fun setDrugId(id: Long) {

        runBlocking{ drug = repo.findDrugById(id) }
    }

    fun deleteDrug() {
        launch {
            repo.deleteDrug(drug)
        }
    }

    fun addDrug(name: String, per24: String, gap: String, isActive: Boolean) {

        val per24Int = Integer.parseInt(per24).toLong()
        val gapInt = Integer.parseInt(gap).toLong()

        runBlocking { repo.insertDrug(Drug(name, per24Int, gapInt, isActive )) }
    }

    fun updateDrug(name: String, per24: String, gap: String, isActive: Boolean) {
        val per24Int = Integer.parseInt(per24).toLong()
        val gapInt = Integer.parseInt(gap).toLong()
        drug.name = name
        drug.gapMinutes =  gapInt
        drug.dosesPerDay = per24Int
        drug.active = isActive
        repo.updateDrug(drug)

    }




}