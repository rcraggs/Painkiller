package com.rcraggs.doubledose.model

import android.arch.persistence.room.*

import java.util.*

@Entity(tableName = "dose")
class Dose (
        @TypeConverters(MedicineTypeConverter::class)
        @ColumnInfo(name="type")
        var type: Medicine = Medicine.EMPTY){

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    @TypeConverters(DateConverter::class)
    @ColumnInfo(name="taken")
    var taken: Date = Calendar.getInstance().time

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
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}

class MedicineTypeConverter {

    @TypeConverter
    fun toConsumptionType(ordinal : Int): Medicine{
        return Medicine.values()[ordinal]
    }

    @TypeConverter
    fun toInt(type : Medicine) : Int {
        return type.ordinal
    }
}

enum class Medicine private constructor(val code: Int) {
    PARACETAMOL(0),
    IBROPRUFEN(1),
    EMPTY(-1)
}