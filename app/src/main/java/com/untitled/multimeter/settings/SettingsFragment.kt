package com.untitled.multimeter.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import com.untitled.multimeter.R
import com.untitled.multimeter.RealmViewModelFactory

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = RealmViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        //get all preferences
        val username = findPreference<EditTextPreference>("username")
        val email = findPreference<EditTextPreference>("email")
        val themeSwitch: SwitchPreferenceCompat? = findPreference("theme mode")
        val frequency = findPreference<ListPreference>("frequency")
        val logoutBtn = findPreference<Preference>("logout")

        //change the users name in the DB
        username?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.updateUsername(newValue.toString())
            true
        }

        //change the users email in the DB
        email?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.updateEmail(newValue.toString())
            true
        }

        //set listener for theme switch
        themeSwitch?.setOnPreferenceChangeListener { _,_ ->
            checkTheme(themeSwitch.isChecked)
            true
        }

        logoutBtn?.setOnPreferenceClickListener {
            logout()
            return@setOnPreferenceClickListener true
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

    private fun logout(){
        viewModel.logout()

        requireActivity().finish()
    }
}