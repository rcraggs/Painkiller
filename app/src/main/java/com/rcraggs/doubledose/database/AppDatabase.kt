package com.rcraggs.doubledose.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.rcraggs.doubledose.model.DateConverter
import com.rcraggs.doubledose.model.Dose

@Database(entities = [Dose::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "doubledose.db")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
    }

    abstract fun doseDao(): DoseDao
}