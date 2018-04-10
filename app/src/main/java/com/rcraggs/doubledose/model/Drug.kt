package com.rcraggs.doubledose.model

data class Drug(
        val maxDosesPer24Hours: Int = 4,
        val hoursBetweenDoses: Long = 2
) {

    companion object {
        @Volatile private var INSTANCE: Drug? = null
        fun getInstance(): Drug = Drug(4)
    }
}