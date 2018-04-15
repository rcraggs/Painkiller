package com.rcraggs.doubledose.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.rcraggs.doubledose.model.Dose
import org.threeten.bp.Instant

@Dao
interface DoseDao {

    @Query("SELECT * FROM dose WHERE drug = :drugId ORDER BY taken desc LIMIT 1")
    fun getLatest(drugId: Long): Dose?

    @Query("SELECT * FROM dose ORDER BY taken desc")
    fun getAll(): List<Dose>

    @Query("SELECT * FROM dose ORDER BY taken desc")
    fun getAllLive(): LiveData<List<Dose>>


    @Query("SELECT * FROM dose WHERE drug = :drugId ORDER BY taken desc")
    fun getAllLive(drugId: Long): LiveData<List<Dose>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(d: Dose) : Long

    @Delete
    fun delete(d: Dose)

    @Query("DELETE FROM dose")
    fun deleteAll()

    @Query("SELECT * FROM dose ORDER BY taken desc LIMIT 1")
    fun getLatest(): LiveData<Dose>

    @Query("SELECT * FROM dose WHERE drug = :drugId AND taken > :date ORDER BY taken DESC")
    fun getDosesSince(drugId: Long, date: Instant): List<Dose>

    @Query("SELECT * FROM dose WHERE drug = :drugId ORDER BY taken desc")
    fun getAll(drugId: Long): List<Dose>

    @Query("DELETE FROM dose WHERE id = :doseId")
    fun delete(doseId: Long)

    @Query("SELECT * FROM dose WHERE id = :doseId")
    fun findDoseById(doseId: Long): Dose

    @Update
    fun update(dose: Dose)
}