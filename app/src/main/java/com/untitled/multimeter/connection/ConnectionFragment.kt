package com.untitled.multimeter.connection

import android.content.Intent
import android.graphics.Color
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

/**
 * Connection UI. This class handles and displays the
 * connection status of the external multimeter device.
 */
class ConnectionFragment : Fragment() {
    private var connected: Boolean = false
    private lateinit var root: View
    private lateinit var connectionStatusText: TextView
    private lateinit var connectionHelpText: TextView
    private lateinit var connectionButton: Button
    private lateinit var measureButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Debug", "CONNECT: onCreate")

        if (savedInstanceState != null) {
            connected = savedInstanceState.getBoolean("connected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Debug", "CONNECT: onCreateView")
        root = inflater.inflate(R.layout.fragment_connection, container, false)

        if (!connected) {
//            connectionStatusText = root.findViewById(R.id.connection_status)
//            connectionHelpText = root.findViewById(R.id.connection_help)
//            measureButtonSetup()
//            connectButtonSetup()
        }

        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("Debug", "CONNECT: onSavedInstanceState")
        if (connected) {
            outState.putBoolean("connected", true)
        } else {
            outState.putBoolean("connected", false)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Debug", "CONNECT: Session resumed.")

        if (connected) {
            connectionStatusText.text = "Connected"
            connectionHelpText.text = "Connected to: Unknown Device"
            connectionButton.text = "Change Devices"
            connectionStatusText.setTextColor(Color.parseColor("#4BB543"))
            measureButton.isVisible = true
        }
    }

    /**
     * Sets up the "Measure" button and attaches a listener to it
     */
    private fun measureButtonSetup() {
        measureButton = root.findViewById(R.id.measure_btn)
        measureButton.isVisible = false
        measureButton.setOnClickListener {
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_main, MeasurementFragment())
            transaction?.commit()
        }
    }

    /**
     * Sets up the "Connect" button and attaches a listener to it
     */
    private fun connectButtonSetup() {
        connectionButton = root.findViewById(R.id.connection_btn)
        connectionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SETTINGS))
            connected = true
        }
    }
}