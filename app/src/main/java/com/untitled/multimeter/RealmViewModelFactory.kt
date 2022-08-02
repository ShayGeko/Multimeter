package com.untitled.multimeter

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.untitled.multimeter.createaccount.CreateAccountViewModel
import com.untitled.multimeter.createexperiment.CreateExperimentViewModel
import com.untitled.multimeter.experiments.ExperimentViewModel
import com.untitled.multimeter.invitations.InvitationsViewModel
import com.untitled.multimeter.login.LoginViewModel

class UserViewModelFactory(
    private val application : Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java))
            return LoginViewModel((application as MultimeterApp).userRepository) as T
        if(modelClass.isAssignableFrom(CreateAccountViewModel::class.java))
            return CreateAccountViewModel((application as MultimeterApp).userRepository) as T
        if(modelClass.isAssignableFrom(ExperimentViewModel::class.java))
            return ExperimentViewModel((application as MultimeterApp).experimentRepository) as T
        if(modelClass.isAssignableFrom(InvitationsViewModel::class.java))
            return InvitationsViewModel((application as MultimeterApp).userRepository, application.experimentRepository) as T
        if(modelClass.isAssignableFrom(CreateExperimentViewModel::class.java))
            return CreateExperimentViewModel((application as MultimeterApp).userRepository, application.experimentRepository) as T


        throw IllegalArgumentException("Unknown ViewModel class")
    }
}