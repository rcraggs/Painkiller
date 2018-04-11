package com.rcraggs.doubledose.database

import android.arch.persistence.room.Room
import android.content.Context
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugStatus
import com.rcraggs.doubledose.util.dayAgo
import org.threeten.bp.Instant


class AppRepo(private val context: Context) {
    //todo not allow queries on main thread
    val db by lazy { Room
            .databaseBuilder(context, AppDatabase::class.java, "dose2.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .addCallback(AppDbCallback())
            .build()}

    /**
     * For a drug, create the status based on the doses that have been taken
     */
    fun getDrugStatus(drug: Drug): DrugStatus {

        val ds = DrugStatus(drug)
        refreshDrugStatus(ds)
        return ds
    }

    fun refreshDrugStatus(status: DrugStatus) {

        status.dosesIn24Hours = db.doseDao().getDosesSince(status.drug.id, Instant.now().dayAgo()).size
        status.timeOfLastDose = db.doseDao().getLatest(status.drug.id)?.taken
    }
}