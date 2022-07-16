package com.untitled.multimeter

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.untitled.multimeter.createAccount.CreateAccountViewModel
import com.untitled.multimeter.login.LoginViewModel

class UserViewModelFactory(
    private val application : Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java))
            return LoginViewModel((application as MultimeterApp).userRepository) as T
        if(modelClass.isAssignableFrom(CreateAccountViewModel::class.java))
            return CreateAccountViewModel((application as MultimeterApp).userRepository) as T


        throw IllegalArgumentException("Unknown ViewModel class")
    }
}