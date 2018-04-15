package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose

@Suppress("MemberVisibilityCanBePrivate")
class HistoryViewModel(val repo: AppRepo): ViewModel(){

    lateinit var doses: LiveData<List<Dose>>
    lateinit var drugName: String

    fun start(drugId: Long = -1L) {

        doses = when {
            drugId != -1L -> {

                val drug = repo.db.drugDao().findById(drugId)
                Transformations.map(repo.db.doseDao().getAllLive(drugId), {
                        it.forEach {
                            it.drug = drug
                        }
                    it
                    }
                )
            }
            else -> {
                val drugs = repo.db.drugDao().getAll()
                Transformations.map(repo.db.doseDao().getAllLive(), {
                    it.forEach {
                        it.drug = drugs.find {it.id == drugId}!!
                    }
                    it
                })
            }
        }

        drugName = repo.getDrugWithId(drugId)?.name ?: "All Drugs"
    }
}