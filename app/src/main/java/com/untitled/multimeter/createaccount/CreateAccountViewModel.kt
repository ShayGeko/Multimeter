package com.untitled.multimeter.createaccount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.data.model.CreateAccountModel
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.*

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
    fun registerUser(createAccountModel: CreateAccountModel) : LiveData<Result<Unit>> {
        return repository.register(createAccountModel) as MutableLiveData<Result<Unit>>
    }

    /**
     * Registers device for push notifications from FCM to notify user about collaboration invitations
     *
     * @param applicationContext application context
     * @param userInfo info about current user
     */
    fun registerDeviceForPushNotifications(){

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
                val userInfo = withContext(Dispatchers.Default) {
                    repository.getCurrentUserInfoAsync()
                }
                repository.addOrUpdateDeviceToken(userInfo, token)
            }
        })

    }
}