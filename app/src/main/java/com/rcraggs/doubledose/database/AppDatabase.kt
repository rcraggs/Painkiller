package com.rcraggs.doubledose.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.rcraggs.doubledose.model.DateConverter
import com.rcraggs.doubledose.model.Dose

@Database(entities = [Dose::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doseDao(): DoseDao
}