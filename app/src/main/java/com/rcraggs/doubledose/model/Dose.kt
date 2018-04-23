package com.rcraggs.doubledose.model

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import org.threeten.bp.Instant

import java.util.*

@Entity(tableName = "dose",
        foreignKeys =
            [ForeignKey(
                entity = Drug::class,
                parentColumns = ["id"],
                childColumns = ["drug"],
                onDelete = CASCADE)],
        indices = [Index("drug")]
        )
class Dose() {

    constructor(drug: Drug) : this() {
        this.drugId = drug.id
        this.drug = drug
    }

    constructor(drug: Drug, time: Instant): this(drug) {
        this.taken = time
    }

    @Transient
    lateinit var drug: Drug

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    @ColumnInfo(name="drug")
    var drugId: Long = 0

    @TypeConverters(DateConverter::class)
    @ColumnInfo(name="taken")
    var taken = Instant.now()!!

    override fun equals(other: Any?): Boolean {
        return if (other is Dose)
            other.taken == this.taken &&
                    other.drugId == this.drugId
        else
            false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + taken.hashCode()
        result = 31 * result + drugId.hashCode()
        return result
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
}