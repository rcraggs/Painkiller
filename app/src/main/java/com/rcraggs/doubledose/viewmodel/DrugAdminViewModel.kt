package com.rcraggs.doubledose.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.rcraggs.doubledose.database.AppRepo

class DrugAdminViewModel(repo: AppRepo, application: Application) : AndroidViewModel(application){
    val drugs = repo.getAllDrugsLive()
}