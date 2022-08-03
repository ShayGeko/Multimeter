package com.untitled.multimeter.connection

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.untitled.multimeter.MainFragment
import com.untitled.multimeter.R
import com.untitled.multimeter.mesurement.MeasurementFragment

/**
 * Connection UI. This class handles and displays the
 * connection status of the external multimeter device.
 */
class ConnectionFragment : Fragment() {
    private lateinit var root: View
    private lateinit var connectionStatusText: TextView
    private lateinit var connectionHelpText: TextView
    private lateinit var connectionButton: Button
    private lateinit var measureButton: Button
    private val PERMISSION_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Main Fragment State", MainFragment.state)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_connection, container, false)

        connectionStatusText = root.findViewById(R.id.connection_status)
        connectionHelpText = root.findViewById(R.id.connection_help)
        measureButtonSetup()
        connectButtonSetup()
        checkLocationPermissions()

        return root
    }

    /**
     * Handles state of connection fragment on resume.
     */
    override fun onResume() {
        if (MainFragment.state == MainFragment.CONNECTED) {

            // Get SSID from Connected Network
            val ssid = getSSID()
            val connectionUpdate = "Connected to: $ssid"
            connectionStatusText.text = "Connected"
            connectionHelpText.text = connectionUpdate
            connectionButton.text = "Change Devices"
            connectionButton.setBackgroundColor(Color.GRAY)
            connectionStatusText.setTextColor(Color.parseColor("#4BB543"))
            measureButton.isVisible = true
        }
        super.onResume()
    }

    /**
     * Sets up the "Connect" button and attaches a listener to it
     */
    private fun connectButtonSetup() {
        connectionButton = root.findViewById(R.id.connection_btn)
        connectionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SETTINGS))
            MainFragment.state = MainFragment.CONNECTED
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
            transaction?.replace(R.id.fragment_container, MeasurementFragment())
            transaction?.commit()
            MainFragment.state = MainFragment.MEASURE
        }
    }

    private fun getSSID(): String {
        val manager = requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        return info.ssid
    }

    private fun checkLocationPermissions() {
        val context = requireActivity().applicationContext
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }
}