package com.untitled.multimeter.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.untitled.multimeter.MainMenuActivity
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.R
import com.untitled.multimeter.UserViewModelFactory
import com.untitled.multimeter.createaccount.CreateAccountActivity
import io.realm.kotlin.mongodb.exceptions.ConnectionException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn : Button
    private lateinit var registerBtn : Button
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val viewModelFactory = UserViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        // get the views
        emailEditText = findViewById(R.id.email_edittext)
        passwordEditText = findViewById(R.id.password_edittext)
        loginBtn = findViewById(R.id.login_btn)
        registerBtn = findViewById(R.id.register_btn)

        // add listeners
        loginBtn.setOnClickListener { login() }
        registerBtn.setOnClickListener { registerUser() }

        // observe login result
        // has to be added here, and not directly after asking sending login request
        // so that the observer can still fetch the data after screen rotations
        // addLoginResultObserver()
    }

    /**
     * Attempts to login the user
     * with username and password acquired from editTexts
     * opens mainActivity if login was successful,
     * or displays the error in a Toast otherwise
     */
    private fun login(){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // simple input validation
        if(email.isEmpty() || password.isEmpty()){
            showErrorMessage("Please enter both login and password!")
            return
        }

        // disable the login button while waiting response from server,
        // so that the user cannot send several login requests
        loginBtn.isEnabled = false

        // send login request, await response
        viewModel.login(email, password).observe(this) {
            Log.d(APPLICATION_TAG, "response from login acquired")

            // once request is received, enable the login button
            loginBtn.isEnabled = true

            // if result is successful, return to the main activity
            it.onSuccess { userInfo ->

                Toast.makeText(this, "Welcome, ${userInfo.userName}", Toast.LENGTH_LONG).show()
                startActivity(Intent(application, MainMenuActivity::class.java))
                finish()
            }
            // otherwise, display error to the user
            it.onFailure { ex : Throwable ->
                when(ex){
                    is InvalidCredentialsException -> {
                        showErrorMessage("Invalid username or password")
                    }
                    is ConnectionException -> {showErrorMessage("Could not connect to the server. Please check your internet connection")}
                    else -> {
                        Log.e(APPLICATION_TAG, "Error: $ex")
                    }
                }
            }
        }
    }

    /**
     * Opens the "createAccount" activity
     */
    private fun registerUser(){
        startActivity(Intent(application, CreateAccountActivity::class.java))
    }
    /**
     * Displays a toast with the specified error message
     *
     * @param msg - error message
     */
    private fun showErrorMessage(msg : String){
        val toast = Toast.makeText(this,msg, Toast.LENGTH_LONG)
        toast.show()
    }
}