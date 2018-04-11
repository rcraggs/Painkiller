package com.rcraggs.doubledose.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.RoomDatabase
import android.content.ContentValues
import android.util.Log

class AppDbCallback: RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        val ibroprufen = ContentValues()
        ibroprufen.put("name", "Ibroprufen")
        ibroprufen.put("perday", 4L)
        ibroprufen.put("gap", 120L)
        db.insert("drug", OnConflictStrategy.IGNORE, ibroprufen)

        val paracetamol = ContentValues()
        paracetamol.put("name", "Paracetamol")
        paracetamol.put("perday", 4L)
        paracetamol.put("gap", 120L)
        db.insert("drug", OnConflictStrategy.IGNORE, paracetamol)

        Log.d("AppDbCallback", "Adding data to new database")
    }
}