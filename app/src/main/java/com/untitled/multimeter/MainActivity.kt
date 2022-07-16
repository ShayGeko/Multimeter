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
import io.realm.kotlin.internal.platform.isFrozen
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

        // get the application user
        val user = realmApp.currentUser
        // if no user, or if login expired, prompt login
        if(user == null || !user.loggedIn){
            startActivity(Intent(this, LoginActivity::class.java))
        }
        else {

            Log.d(APPLICATION_TAG, user.toString())

            Toast.makeText(this, "Welcome, ${user.identity}", Toast.LENGTH_LONG).show()
        }
    }
}