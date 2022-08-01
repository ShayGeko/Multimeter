package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.MultimeterApp.Companion.REALM_PARTITION
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.UserInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Repository for users
 *
 */
class UserRepository {
    private lateinit var mRealm : Realm
    /**
     * Attempts to login the user with the specified email and password
     *
     * @param email - email
     * @param password - password
     * @returns
     * LiveData of acquired User wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun login(email: String, password: String) : LiveData<Result<User>> {
        val loginResult = MutableLiveData<Result<User>> ()
        CoroutineScope(Dispatchers.IO).launch {
            // fancy try-catch block
            runCatching {
                // create the Credentials object from username and password
                val credentials = Credentials.emailPassword(email, password)

                // login, get the user with matched credentials or throw an exception
                // output will be wrapped in Result, because of runCatching{}
                realmApp.login(credentials)
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
    /**
     * Attempts to register the user with the specified email and password
     *
     * @param email - email
     * @param password - password
     * @returns
     */
    fun register(email: String, password: String) : LiveData<Result<Unit>> {
        val registerResult = MutableLiveData<Result<Unit>> ()
        CoroutineScope(Dispatchers.IO).launch {
            // fancy try-catch block
            runCatching {

                // try to register the user with the provided email and password
                realmApp.emailPasswordAuth.registerUser(email, password)


                // create the Credentials object from username and password
                val credentials = Credentials.emailPassword(email, password)

                // login, get the user with matched credentials or throw an exception
                // output will be wrapped in Result, because of runCatching{}
                realmApp.login(credentials)


            } // if no exception was thrown, propagate the successful Result
                .onSuccess {

                    registerResult.postValue(Result.success(Unit))
                } // otherwise, propagate the failed result
                .onFailure { exception : Throwable ->
                    registerResult.postValue(Result.failure(exception))
                }
        }

        return registerResult
    }

    fun addUserInfo(userInfo: UserInfo){
        CoroutineScope(Dispatchers.IO).launch {

            runCatching {
                // create the Credentials object from username and password
                val config = SyncConfiguration.Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(UserInfo::class))
                    .build()


                mRealm = Realm.open(config)
//                    val query = .executeTransactionAsync {
//                        // using our thread-local new realm instance, query for and update the task status
//                        val item = it.where<Task>().equalTo("id", id).findFirst()
//                        item?.statusEnum = status
//                    }
                mRealm.writeBlocking {
                    this.copyToRealm(userInfo)


                    userInfo.userName = "some updated username"

                    return@writeBlocking userInfo
                }


            } // if no exception was thrown, propagate the successful Result
                .onSuccess { userInfo ->
                    Log.d(APPLICATION_TAG, "UserInfo insert succesful")
                    Log.d(APPLICATION_TAG, "new username: ${userInfo.userName}")
                } // otherwise, propagate the failed result
                .onFailure { exception : Throwable ->
                    Log.e(APPLICATION_TAG, "UserInfo insert failed")
                    Log.e(APPLICATION_TAG, exception.message.toString())
                }

            mRealm.close()

            // fancy try-catch block

        }
    }
}