package com.rcraggs.doubledose.database

import android.arch.persistence.room.*
import com.rcraggs.doubledose.model.Drug

@Dao
interface DrugDao {

    // todo live data to update the UI when drugs are changed

    @Query("SELECT * FROM drug ORDER BY name desc")
    fun getAll(): List<Drug>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(d: Drug) : Long

    @Delete
    fun delete(d: Drug)

    @Query("SELECT * FROM drug WHERE id = :drugId LIMIT 1")
    fun findById(drugId: Long): Drug
}