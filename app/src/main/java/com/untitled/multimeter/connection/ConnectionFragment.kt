package com.untitled.multimeter.connection

import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.untitled.multimeter.R


class ConnectionFragment : Fragment() {
    private lateinit var root: View
    private lateinit var connectionStatus: TextView
    private lateinit var connectionHelp: TextView
    private lateinit var connectionButton: Button
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // https://developer.android.com/training/monitoring-device-state/connectivity-status-type

        // Network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

        // Lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_connection, container, false)
        connectionStatus = root.findViewById(R.id.connection_status)
        connectionHelp = root.findViewById(R.id.connection_help)
        connectButtonSetup()

        return root
    }

    override fun onResume() {
        super.onResume()
        Log.d("Debug", "Session resumed.")

//        val networkRequest = NetworkRequest.Builder()
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            .build()
//
//        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
//        connectivityManager.requestNetwork(networkRequest, networkCallback)

    }

    private fun connectButtonSetup() {
        connectionButton = root.findViewById(R.id.connection_btn)
        connectionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SETTINGS))
            connectionStatus.text = "Connected"
            connectionHelp.text = "Connected to: Device"
            connectionButton.text = "Change Devices"
            connectionStatus.setTextColor(Color.parseColor("#4BB543"))
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_layout_id, root)
//            transaction.commit()
        }
    }
}