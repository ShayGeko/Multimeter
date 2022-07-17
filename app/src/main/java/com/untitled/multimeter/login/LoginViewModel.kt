package com.untitled.multimeter.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.User

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    var loginResult = MutableLiveData<Result<User>>()
    /**
     * Attempts to login the user with the specified username and password
     *
     * @param username username
     * @param password password
     *
     * @return
     * LiveData of acquired User wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun login(username: String, password: String) : LiveData<Result<User>> {
        loginResult = repository.login(username, password) as MutableLiveData<Result<User>>
        return loginResult
    }

}