package com.untitled.multimeter.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.untitled.multimeter.R

class SettingsFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPref = requireActivity().applicationContext.getSharedPreferences("sharedPrefsKey", AppCompatActivity.MODE_PRIVATE)
        val saveBtn = view.findViewById<Button>(R.id.saveBtn)

        loadPreferences()

        saveBtn.setOnClickListener {
            savePreferences()
            Toast.makeText(requireContext(), "Saved Settings", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadPreferences() {
        view?.findViewById<EditText>(R.id.editTextUsername)?.setText(
            sharedPref.getString("usernameKey", "").toString()
        )
        view?.findViewById<EditText>(R.id.editTextName)
            ?.setText(sharedPref.getString("nameKey", "").toString())
        view?.findViewById<Switch>(R.id.switchLightDarkMode)?.isChecked =
            sharedPref.getBoolean("switchKey", false)
        view?.findViewById<Spinner>(R.id.spinnerFrequency)?.setSelection(
            sharedPref.getLong("frequencyKey", 0).toInt()
        )
    }

    private fun savePreferences() {
        val editor = sharedPref.edit()

        val username: String = view?.findViewById<EditText>(R.id.editTextUsername)?.text.toString()
        val name: String = view?.findViewById<EditText>(R.id.editTextName)?.text.toString()
        val switch: Boolean = view?.findViewById<SwitchCompat>(R.id.switchLightDarkMode)!!.isChecked
        val frequency: Long = (view?.findViewById<Spinner>(R.id.spinnerFrequency)?.selectedItemId ?:
        editor.putString("usernameKey", username)) as Long
        editor.putString("nameKey", name)
        editor.putBoolean("switchKey", switch)
        editor.putLong("frequencyKey", frequency)

        editor.apply()
    }
}