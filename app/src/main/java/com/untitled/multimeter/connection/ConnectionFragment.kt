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
import androidx.fragment.app.Fragment
import com.untitled.multimeter.R


class ConnectionFragment : Fragment() {
    private lateinit var root: View
    private lateinit var connectionStatus: TextView
    private lateinit var connectionHelp: TextView
    private lateinit var connectionButton: Button

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

    /**
     * Set's up the "connect" button, adds a listener to it
     */
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