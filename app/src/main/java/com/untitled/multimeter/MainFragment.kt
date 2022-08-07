package com.untitled.multimeter

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.untitled.multimeter.connection.ConnectionFragment
import com.untitled.multimeter.mesurement.MeasurementFragment
import java.net.InetAddress

/**
 * Main Fragment. This class handles the state of the
 * main fragment depending on the connection status of
 * the external multimeter device.
 */
class MainFragment : Fragment() {
    companion object {
        var state: String = "Connect"
        const val CONNECT: String = "Connect"
        const val CONNECTED: String = "Connected"
        const val MEASURE: String = "Measure"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            state = savedInstanceState.getString("state").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("state", state)
    }

    /**
     * Checks the state of the main fragment and displays the corresponding
     * interface.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (state) {
            MEASURE -> {
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, MeasurementFragment())
                transaction.commit()
            }
            CONNECT -> {
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, ConnectionFragment())
                transaction.commit()
            }
            CONNECTED -> {
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, ConnectionFragment())
                transaction.commit()
            }
        }
    }
}