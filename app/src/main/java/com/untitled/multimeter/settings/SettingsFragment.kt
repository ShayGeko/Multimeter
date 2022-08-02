package com.untitled.multimeter.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.preference.*
import com.untitled.multimeter.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        val username = findPreference<EditTextPreference>("username")
        val name = findPreference<EditTextPreference>("name")
        val themeSwitch: SwitchPreferenceCompat? = findPreference("theme mode")
        val frequency = findPreference<ListPreference>("frequency")
        val logoutBtn = findPreference<Preference>("logout")

        //set listener for theme switch
        themeSwitch?.setOnPreferenceChangeListener { _,_ ->
            checkTheme(themeSwitch.isChecked)
            true
        }
    }

    private fun checkTheme(input: Boolean) {
        if (input) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }
    }
}