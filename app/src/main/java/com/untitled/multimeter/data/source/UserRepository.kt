package com.untitled.multimeter.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Repository for users
 *
 */
class UserRepository {

    /**
     * Attempts to login the user with the specified login and password
     *
     * @param username - username
     * @param password - password
     * @returns
     */
    fun login(username: String, password: String) : LiveData<Result<User>> {
        val loginResult = MutableLiveData<Result<User>> ()
        CoroutineScope(Dispatchers.IO).launch {
            // fancy try-catch block
            runCatching {
                // create the Credentials object from username and password
                val credentials = Credentials.emailPassword(username, password)

                // login, get the user with matched credentials or throw an exception
                // output will be wrapped in Result, because of runCatching{}
                MultimeterApp.realmApp.login(credentials)
            } // if no exception was thrown, propagate the successful Result
            .onSuccess { user : User ->
                    loginResult.postValue(Result.success(user))
            } // otherwise, propagate the failed result
            .onFailure { exception : Throwable ->
                loginResult.postValue(Result.failure(exception))
            }
        }

        return loginResult
    }
}