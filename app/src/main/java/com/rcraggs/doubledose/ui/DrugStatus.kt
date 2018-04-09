package com.rcraggs.doubledose.ui

import com.rcraggs.doubledose.model.Medicine
import java.util.*

data class DrugStatus(val name: CharSequence, val type: Medicine ) {

    var dosesIn24Hours: Int = 0

}