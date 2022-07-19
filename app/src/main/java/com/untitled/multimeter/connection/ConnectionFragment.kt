package com.untitled.multimeter.connection

import android.content.Intent
import android.graphics.Color
import android.icu.util.Measure
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.untitled.multimeter.R
import com.untitled.multimeter.mesurement.MeasurementFragment


class ConnectionFragment : Fragment() {
    private lateinit var root: View
    private lateinit var connectionStatusText: TextView
    private lateinit var connectionHelpText: TextView
    private lateinit var connectionButton: Button
    private lateinit var measureButton: Button
    private var connected: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_connection, container, false)
        connectionStatusText = root.findViewById(R.id.connection_status)
        connectionHelpText = root.findViewById(R.id.connection_help)
        measureButtonSetup()
        connectButtonSetup()

        return root
    }

    /**
     * Set's up the "connect" button, adds a listener to it
     */
    override fun onResume() {
        super.onResume()
        Log.d("Debug", "Session resumed.")

        if (connected) {
            connectionStatusText.text = "Connected"
            connectionHelpText.text = "Connected to: Unknown Device"
            connectionButton.text = "Change Devices"
            connectionStatusText.setTextColor(Color.parseColor("#4BB543"))
            measureButton.isVisible = true
        }
    }

    private fun measureButtonSetup() {
        measureButton = root.findViewById(R.id.measure_btn)
        measureButton.isVisible = false
        measureButton.setOnClickListener {
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_main, MeasurementFragment())
            transaction?.commit()
        }
    }

    private fun connectButtonSetup() {
        connectionButton = root.findViewById(R.id.connection_btn)
        connectionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SETTINGS))
            connected = true
        }
    }
}