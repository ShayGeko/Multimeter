package com.untitled.multimeter.connection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.untitled.multimeter.R

class ConnectionFragment : Fragment() {
    private lateinit var root: View
    private lateinit var connectionText: TextView
    private lateinit var connectionButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_connection, container, false)
        connectionText = root.findViewById(R.id.connection_text)

        connectionButton = root.findViewById(R.id.connection_btn)
        connectionButton.setOnClickListener {
            connectionText.text = "Connected."
        }

        return root
    }
}