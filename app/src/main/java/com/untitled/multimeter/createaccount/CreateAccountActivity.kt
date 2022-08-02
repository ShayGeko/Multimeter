package com.untitled.multimeter.createaccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.untitled.multimeter.MainMenuActivity
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.R
import com.untitled.multimeter.UserViewModelFactory
import com.untitled.multimeter.data.model.CreateAccountModel
import com.untitled.multimeter.data.model.UserInfo
import io.realm.kotlin.mongodb.exceptions.ConnectionException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import io.realm.kotlin.types.ObjectId

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var createAccountBtn : Button
    private lateinit var viewModel: CreateAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // get the viewmodel
        val viewModelFactory = UserViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CreateAccountViewModel::class.java)

        // get the views
        emailEditText = findViewById(R.id.email_edittext)
        usernameEditText = findViewById(R.id.username_edittext)
        passwordEditText = findViewById(R.id.password_edittext)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext)
        createAccountBtn = findViewById(R.id.create_account_btn)

        // add listeners
        createAccountBtn.setOnClickListener { registerUser() }
    }

    /**
     * Attempts to login the user
     * with username and password acquired from editTexts
     * opens mainActivity if login was successful,
     * or displays the error in a Toast otherwise
     */
    private fun registerUser(){
        val email = emailEditText.text.toString()
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // make sure all the fields are filled
        if(email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            showErrorMessage("Please fill out every field")
            return
        }

        // password rules enforced by MongoDB auth
        // password must be between 6 and 127 characters long
        if(password.length < 6){
            showErrorMessage("Password must be at least 6 characters long")
        }
        else if(password.length > 128){
            showErrorMessage("Password is too long")
        }

        // make sure the passwords match
        if(password != confirmPassword){
            showErrorMessage("Passwords do not match!")
            return
        }

        val createAccountModel = CreateAccountModel(email, username, password)

        // disable the createAccount button while waiting response from server,
        // so that the user cannot send several register requests
        createAccountBtn.isEnabled = false;

        // send register request, await response
        viewModel.registerUser(createAccountModel).observe(this) {

            // re-enable the button upon response
            createAccountBtn.isEnabled = true

            // if result is successful, return to the main activity
            it.onSuccess {
                Toast.makeText(this, "Welcome, ${createAccountModel.username}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainMenuActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            // otherwise, display error to the user
            it.onFailure { ex : Throwable ->
                when(ex){
                    is UserAlreadyExistsException -> {
                        showErrorMessage("User with the specified email already exists")
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
     * Displays a toast with the specified error message
     *
     * @param msg - error message
     */
    private fun showErrorMessage(msg : String){
        val toast = Toast.makeText(this,msg, Toast.LENGTH_LONG)
        toast.show()
    }
}