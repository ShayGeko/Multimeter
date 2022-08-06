package com.untitled.multimeter.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.data.source.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel(){

    /**
     * logs current user out, removes device registration token from the db
     */
    fun logout(){
        val user = MultimeterApp.realmApp.currentUser

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Default) {
                userRepository.removeDeviceRegistrationTokenFromCurrentUser()
            }
            Log.d(APPLICATION_TAG, "removed device token, proceeding to log out")
            user?.logOut()

            if(user == null || !user.loggedIn){
                Log.i(APPLICATION_TAG, "logout successful")
            }
        }
    }


    /**
     * Updates the user's username
     *
     * @param username new username
     */
    fun updateUsername(username : String) {

    }

    /**
     * Updates the user's email
     *
     * @param email new username
     */
    fun updateEmail(email : String) {

    }

}