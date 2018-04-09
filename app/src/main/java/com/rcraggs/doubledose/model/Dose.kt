package com.rcraggs.doubledose.model

import android.arch.persistence.room.*
import org.threeten.bp.Instant

import java.util.*

@Entity(tableName = "dose")
class Dose (
        @ColumnInfo(name="type")
        var type: String = ""){

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    @TypeConverters(DateConverter::class)
    @ColumnInfo(name="taken")
    var taken = Instant.now()

    override fun toString(): String {
        return "$type at $taken"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Dose)
            other.taken == this.taken &&
                    other.type == this.type
        else
            false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + taken.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant): Long {
        return date.toEpochMilli()
    }
}