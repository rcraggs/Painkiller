package com.rcraggs.doubledose.database

import android.arch.persistence.room.Room
import android.content.Context

class AppRepo(private val context: Context) {
    //todo not allow queries on main thread
    val db by lazy { Room.databaseBuilder(context, AppDatabase::class.java, "dose.db").allowMainThreadQueries().build()}
}