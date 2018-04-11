package com.rcraggs.doubledose.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug

@Database(entities = [Dose::class, Drug::class], version = 3, exportSchema = false)
@TypeConverters(Dose.DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doseDao(): DoseDao
    abstract fun drugDao(): DrugDao
}