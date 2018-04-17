package com.rcraggs.doubledose.viewmodel

import android.arch.lifecycle.ViewModel
import com.rcraggs.doubledose.database.AppRepo
import com.rcraggs.doubledose.model.Dose
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.util.Constants
import com.rcraggs.doubledose.util.INotificationsService
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class DoseEditViewModel(private val repo: AppRepo, private val notifications: INotificationsService): ViewModel(){

    private var newHours: Int = 0
    private var newMinutes: Int = 0
    private var newYear: Int = 0
    private var newMonth: Int = 0
    private var newDay: Int = 0
    private var newDrug: Drug? = null

    lateinit var dose: Dose

    private var drugList: List<Drug> = repo.db.drugDao().getAll()
    fun getDrugs() = drugList

    fun setDose(doseId: Long) {
        dose = repo.db.doseDao().findDoseById(doseId)

        val ldt = dose.taken.atZone(ZoneId.systemDefault())
        newDay = ldt.dayOfMonth
        newMonth = ldt.monthValue
        newYear = ldt.year
        newHours = ldt.hour
        newMinutes = ldt.minute

        newDrug = repo.db.drugDao().findById(dose.drugId)
    }

    fun getDoseDateSeconds() = dose.taken.epochSecond
    fun getDoseHour() = LocalDateTime.ofInstant(dose.taken, ZoneId.systemDefault()).hour
    fun getDoseMinutes() = LocalDateTime.ofInstant(dose.taken, ZoneId.systemDefault()).minute
    fun getDoseTimeString(): CharSequence? {
        return Constants.doseDateFormatter.format(LocalDateTime.ofInstant(dose.taken, ZoneId.systemDefault()))
    }

    fun getUpdatedDoseDate() : String {
        val newTaken = LocalDateTime.of(newYear, newMonth, newDay, newHours, newMinutes)
        return Constants.doseDateFormatter.format(newTaken)
    }

    fun getPositionOfDrugInList() = drugList.indexOfFirst { d -> d.id == newDrug?.id}

    fun setNewDate(year: Int, month: Int, day: Int){
        newYear = year
        newMonth = month
        newDay = day
    }

    fun setNewTime(hour: Int, minute: Int){
        newMinutes = minute
        newHours = hour
    }

    fun setNewDrug(drug: Drug) {
        newDrug = drug
    }


    fun deleteDose() {
        repo.db.doseDao().delete(dose.id)
        repo.rescheduleNotifications()
    }

    fun updateDose() {
        val newTaken = LocalDateTime.of(newYear, newMonth, newDay, newHours, newMinutes)

        dose.drugId = newDrug!!.id
        dose.taken = newTaken.atZone(ZoneId.systemDefault()).toInstant()

        repo.db.doseDao().update(dose)
        repo.rescheduleNotifications()
    }
}