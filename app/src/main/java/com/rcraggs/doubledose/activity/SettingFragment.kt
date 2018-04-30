package com.rcraggs.doubledose.activity

import android.os.Bundle
import android.preference.PreferenceFragment
import com.rcraggs.doubledose.R

class SettingFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_screen)
        val p = findPreference(getString(R.string.pref_show_notifications))
    }


}
