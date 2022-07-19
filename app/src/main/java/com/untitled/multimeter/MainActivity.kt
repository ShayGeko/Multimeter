package com.untitled.multimeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoutBtn = findViewById<Button>(R.id.logout_btn);

        logoutBtn.setOnClickListener {

            val user = realmApp.currentUser

            CoroutineScope(Dispatchers.IO).launch {
                user?.logOut()
            }

            finish()
        }
    }

    override fun onStart() {
        super.onStart()


    }
}