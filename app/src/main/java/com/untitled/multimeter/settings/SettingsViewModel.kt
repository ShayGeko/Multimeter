package com.untitled.multimeter.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.MultimeterApp.Companion.getRealmInstance
import com.untitled.multimeter.data.source.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel(){

    /**
     * logs current user out, removes device registration token from the db
     */
    fun logout() : LiveData<Boolean> {
        val logoutResult = MutableLiveData<Boolean>()
        val user = MultimeterApp.realmApp.currentUser

        CoroutineScope(Dispatchers.IO).launch {
            userRepository.removeDeviceRegistrationTokenFromCurrentUser()
            Log.d(APPLICATION_TAG, "removed device token, proceeding to log out")
            user?.logOut()

            Log.d(APPLICATION_TAG, "Closing the realm")
            getRealmInstance().close()

            if(user == null || !user.loggedIn){
                Log.i(APPLICATION_TAG, "logout successful")
                logoutResult.postValue(true)
            }

            logoutResult.postValue(false)
        }

        return logoutResult
    }


    /**
     * Updates the user's username
     *
     * @param username new username
     */
    fun updateUsername(username : String) {
        userRepository.updateUsername(username)
    }

    /**
     * Updates the user's email
     *
     * @param email new username
     */
    fun updateEmail(email : String) {
        userRepository.updateEmail(email)
    }

}