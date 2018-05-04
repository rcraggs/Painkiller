package com.rcraggs.doubledose.model

import android.arch.persistence.room.*

@Entity(tableName = "drug")
class Drug() {

    @ColumnInfo(name = "name") var name: String = ""
    @ColumnInfo(name = "perday") var dosesPerDay: Long = 4
    @ColumnInfo(name = "gap") var gapMinutes: Long = 240
    @ColumnInfo(name = "active") var active = true

    @Ignore
    constructor(n: String, p: Long = 4, g: Long = 240, a: Boolean = true): this() {
        name = n
        dosesPerDay = p
        gapMinutes = g
        active = a
    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    @Ignore
    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Drug)
            other.id == this.id
        else
            false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}