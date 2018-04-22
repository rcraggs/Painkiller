package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import kotlinx.coroutines.experimental.runBlocking

@Suppress("MemberVisibilityCanBePrivate")
class HistoryViewModel(val repo: AppRepo): ViewModel(){

    lateinit var doses: LiveData<List<Dose>>
    lateinit var drugName: String

    fun start(drugId: Long = -1L) {

        doses = when {
            drugId != -1L -> {

                val drug = runBlocking { repo.findDrugById(drugId)}
                Transformations.map(repo.getAllDosesLive(drugId), {
                        it.forEach {
                            it.drug = drug
                        }
                    it
                    }
                )
            }
            else -> {
                val drugs = runBlocking {  repo.getAllDrugs() }
                Transformations.map(repo.getAllDosesLive(), {
                    it.forEach {
                        it.drug = drugs.find {d -> it.drugId == d.id}!!
                    }
                    it
                })
            }
        }

        drugName = runBlocking { repo.getDrugWithId(drugId)?.name ?: "All Drugs" }
    }
}