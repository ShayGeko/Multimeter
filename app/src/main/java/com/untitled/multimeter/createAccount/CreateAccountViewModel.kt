package com.untitled.multimeter.createAccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.User

class CreateAccountViewModel(private val repository: UserRepository) : ViewModel() {

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
    fun registerUser(username: String, password: String) : LiveData<Result<User>> {
        return repository.login(username, password)
    }

}