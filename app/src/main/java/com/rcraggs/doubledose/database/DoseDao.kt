package com.rcraggs.doubledose.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.rcraggs.doubledose.model.Dose
import org.threeten.bp.Instant

@Dao
interface DoseDao {

    @Query("SELECT * FROM dose WHERE type = :type ORDER BY taken desc LIMIT 1")
    fun getLatest(type: String): LiveData<Dose>

    @Query("SELECT * FROM dose ORDER BY taken desc")
    fun getAll(): List<Dose>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(d: Dose) : Long

    @Delete
    fun delete(d: Dose)

    @Query("DELETE FROM dose")
    fun deleteAll()

    @Query("SELECT * FROM dose ORDER BY taken desc LIMIT 1")
    fun getLatest(): LiveData<Dose>

    @Query("SELECT * FROM dose WHERE type = :type AND taken > :date ORDER BY taken DESC")
    fun getDosesSince(type: String, date: Instant): List<Dose>


}