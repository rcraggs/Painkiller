package com.rcraggs.doubledose.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class DrugWithDoses {

    @Embedded
    var drug: Drug = Drug("")

    @Relation(
            parentColumn = "id",
            entityColumn = "drug",
            entity = Dose::class)
    var doses: List<Dose> = listOf()
}