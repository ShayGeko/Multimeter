package com.untitled.multimeter.settings

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import com.untitled.multimeter.MainFragment
import com.untitled.multimeter.R
import com.untitled.multimeter.RealmViewModelFactory
import kotlin.system.exitProcess
import com.untitled.multimeter.mesurement.MeasurementViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var measurementViewModel: MeasurementViewModel

    private lateinit var themeSwitch: SwitchPreferenceCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = RealmViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
        measurementViewModel = ViewModelProvider(this, viewModelFactory).get(MeasurementViewModel::class.java)
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        //get all preferences
        val username = findPreference<EditTextPreference>("username")
        themeSwitch = findPreference("theme mode")!!
        val frequency = findPreference<ListPreference>("frequency")
        val logoutBtn = findPreference<Preference>("logout")

        //change the users name in the DB
        username?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.updateUsername(newValue.toString())
            true
        }

        //set listener for theme switch
        themeSwitch.setOnPreferenceChangeListener { _, _ ->
            MainScope().launch {
                checkTheme(themeSwitch.isChecked)
                cancel()
            }
            true
        }

        frequency?.setOnPreferenceChangeListener { _, _ ->
            MainScope().launch {
                //use a coroutine task to set rate after internal state changes
                measurementViewModel.setRefreshRate(frequency.value.toFloat())
                cancel()
            }
            true
        }

        logoutBtn?.setOnPreferenceClickListener {
            logout()
            return@setOnPreferenceClickListener true
        }
    }

    private fun checkTheme(input: Boolean) {
        if (input) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            viewModel.setTheme(2)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            viewModel.setTheme(1)
        }
    }

    private fun logout(){
        viewModel.logout().observe(this){
            finishAffinity(requireActivity())

            exitProcess(1);
        }
    }
}