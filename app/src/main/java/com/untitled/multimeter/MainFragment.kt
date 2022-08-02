package com.untitled.multimeter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.untitled.multimeter.connection.ConnectionFragment
import com.untitled.multimeter.mesurement.MeasurementFragment

/**
 * Main Fragment. This class handles the state of the
 * main fragment depending on the connection status of
 * the external multimeter device.
 */
class MainFragment : Fragment() {
    private var state: String = CONNECTION
    companion object {
        const val CONNECTION: String = "Connect"
        const val MEASURE: String = "Measure"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Debug", "MAIN: onCreate")

        if (savedInstanceState != null) {
            state = savedInstanceState.getString("state").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Debug", "MAIN: onCreateView")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    /**
     * Checks the state of the main fragment and displays the corresponding
     * interface.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (state) {
            CONNECTION -> {
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, ConnectionFragment())
                transaction.commit()
            }
            MEASURE -> {
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, MeasurementFragment())
                transaction.commit()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("Debug", "MAIN: onSavedInstanceState")
        outState.putString("state", state)
    }
}