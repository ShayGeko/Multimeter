package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.MultimeterApp.Companion.REALM_PARTITION
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.CreateAccountModel
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
import kotlinx.coroutines.runBlocking

/**
 * Repository for users
 *
 */
class UserRepository {
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
     * @returns [LiveData] of [Result] with nothing inside on success, or with [Throwable] explaining what went wrong on failure
     */
    fun register(account : CreateAccountModel) : LiveData<Result<Unit>> {
        val registerResult = MutableLiveData<Result<Unit>> ()
        CoroutineScope(Dispatchers.IO).launch {
            // fancy try-catch block
            runCatching {

                // try to register the user with the provided email and password
                realmApp.emailPasswordAuth.registerUser(account.email, account.password)


                // create the Credentials object from username and password
                val credentials = Credentials.emailPassword(account.email, account.password)

                // login, get the user with matched credentials or throw an exception
                // output will be wrapped in Result, because of runCatching{}
                realmApp.login(credentials)

                val userInfo = UserInfo().apply {
                    this._id = ObjectId.from(realmApp.currentUser!!.identity)
                    this.email = account.email
                    this.userName = account.username
                }
                addUserInfo(userInfo)

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

    fun getUserInfo(id: ObjectId): LiveData<UserInfo>{
        val resultLiveData = MutableLiveData<UserInfo>()

        CoroutineScope(Dispatchers.IO).launch {

        }

        return resultLiveData
    }

    /**
     * Stores [UserInfo] about just registered user to [Realm
     *
     * @param userInfo information about user to store
     * @return [Result] with inserted [UserInfo] on success or [Result] with [Throwable] explaining what went wrong on failure
     */
    private fun addUserInfo(userInfo: UserInfo){
        val config = SyncConfiguration
            .Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(UserInfo::class))
            .build()
        val mRealm = Realm.open(config)
        mRealm.writeBlocking { this.copyToRealm(userInfo)}
        mRealm.close()
    }
}