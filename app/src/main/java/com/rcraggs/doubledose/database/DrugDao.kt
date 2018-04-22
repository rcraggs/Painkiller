package com.rcraggs.doubledose.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.model.DrugWithDoses

@Dao
interface DrugDao {

    @Query("SELECT * FROM drug ORDER BY name")
    fun getAllDrugsWithDoses(): LiveData<List<DrugWithDoses>>

    @Query("SELECT * FROM drug ORDER BY name")
    fun getAllDrugsWithDosesSync(): List<DrugWithDoses>

    @Query("SELECT * FROM drug ORDER BY name desc")
    fun getAll(): List<Drug>

    @Query("SELECT * FROM drug ORDER BY name desc")
    fun getAllLive(): LiveData<List<Drug>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(d: Drug) : Long

    @Delete
    fun delete(d: Drug)

    @Query("SELECT * FROM drug WHERE id = :drugId LIMIT 1")
    fun findById(drugId: Long): Drug

    @Query("SELECT * FROM drug WHERE id = :id LIMIT 1")
    fun findWithDosesById(id: Long): DrugWithDoses

}