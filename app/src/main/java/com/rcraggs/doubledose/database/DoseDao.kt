package com.rcraggs.doubledose.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.rcraggs.doubledose.model.Dose

@Dao
interface DoseDao {

    @Query("SELECT * FROM dose WHERE type = :type ORDER BY taken desc LIMIT 1")
    fun getLatest(type: String): LiveData<Dose>

    @Query("SELECT * FROM dose ORDER BY taken desc")
    fun getAll(): List<Dose>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(d: Dose)

    @Query("SELECT * FROM dose ORDER BY taken desc LIMIT 1")
    fun getLatest(): LiveData<Dose>
}