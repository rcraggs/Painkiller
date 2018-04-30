package com.rcraggs.doubledose.activity

import android.os.Bundle
import android.preference.PreferenceActivity

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingFragment()).commit()
    }
}
