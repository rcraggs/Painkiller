package com.rcraggs.doubledose.model

import android.arch.persistence.room.*

@Entity(tableName = "drug")
class Drug() {

    @ColumnInfo(name = "name") var name: String = ""
    @ColumnInfo(name = "perday") var dosesPerDay: Long = 4
    @ColumnInfo(name = "gap") var gapMinutes: Long = 240

    @Ignore
    constructor(n: String, p: Long = 4, g: Long = 240): this() {
        name = n
        dosesPerDay = p
        gapMinutes = g
    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    @Ignore
    override fun toString(): String {
        return name
    }
}