package com.untitled.multimeter.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

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
    fun login(username: String, password: String) : LiveData<Result<UserInfo>> {
        return repository.login(username, password) as MutableLiveData<Result<UserInfo>>
    }

    /**
     * Registers device for push notifications from FCM to notify user about collaboration invitations
     *
     * @param userInfo info about current user
     */
    fun registerDeviceForPushNotifications(userInfo: UserInfo){

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(MultimeterApp.APPLICATION_TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "$token"
            Log.d(MultimeterApp.APPLICATION_TAG, msg)


            CoroutineScope(Dispatchers.IO).launch {
                repository.addOrUpdateDeviceToken(userInfo, token)
            }
        })

    }

}