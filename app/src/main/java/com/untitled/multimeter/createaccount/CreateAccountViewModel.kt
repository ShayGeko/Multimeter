package com.untitled.multimeter.createaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.User

class CreateAccountViewModel(private val repository: UserRepository) : ViewModel() {

    /**
     * Attempts to register the user with the specified email and password
     *
     * @param email email
     * @param password password
     *
     * @return
     * LiveData of acquired User wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun registerUser(email: String, password: String) : LiveData<Result<Unit>> {
        return repository.register(email, password) as MutableLiveData<Result<Unit>>
    }
    /**
     * Attempts to login the user with the specified email and password
     *
     * @param email email
     * @param password password
     *
     * @return
     * LiveData of acquired User wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun login(email: String, password: String) : LiveData<Result<User>> {
        return repository.login(email, password)
    }

    /**
     * Adds the addition user info
     *
     */
    fun addUserData(userInfo: UserInfo){

    }
}