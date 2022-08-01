package com.untitled.multimeter.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp.Companion.REALM_PARTITION
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.CreateAccountModel
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.realm.RealmObjectNotFoundException
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
class UserRepository : AutoCloseable {
    lateinit var mRealm : Realm

    /**
     * Attempts to login the user with the specified email and password
     *
     * @param email - email
     * @param password - password
     * @returns
     * LiveData of acquired User wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun login(email: String, password: String) : LiveData<Result<UserInfo>> {
        val loginResult = MutableLiveData<Result<UserInfo>> ()
        CoroutineScope(Dispatchers.IO).launch {
            // fancy try-catch block
            runCatching {
                // create the Credentials object from username and password
                val credentials = Credentials.emailPassword(email, password)

                // login, get the user with matched credentials or throw an exception
                // output will be wrapped in Result, because of runCatching{}
                realmApp.login(credentials)

                getUserInfoBlocking()
            } // if no exception was thrown, propagate the successful Result
            .onSuccess { user : UserInfo ->
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

    /**
     * **Should only be called if the user is logged in**
     *
     * @return Observable [Result] of query for [UserInfo] for the currently logged in user
     */
    fun getUserInfo(): LiveData<Result<UserInfo>>{
        val resultLiveData = MutableLiveData<Result<UserInfo>>()
        CoroutineScope(Dispatchers.IO).launch {
            // fancy try-catch
            runCatching {
                // get the configuration using current user, so that non-logged in user cannot write to the db
                // use application-wise partition, and limit the visible scope only to userInfo
                initRealm()

                // get id of the logged in user
                val userId = ObjectId.from(realmApp.currentUser!!.identity)

                // find first userInfo matching user's id, throw an exception if none was found
                return@runCatching mRealm
                    .query<UserInfo>("_id == $0", userId)
                    .first().find()
                    ?: throw RealmObjectNotFoundException("UserInfo for the current user is not found!")
            }// if no exception was thrown, propagate the successful Result
                .onSuccess { userInfo ->
                    resultLiveData.postValue(Result.success(userInfo))
                } // otherwise, propagate the failed result
                .onFailure { exception : Throwable ->
                    resultLiveData.postValue(Result.failure(exception))
                }
        }

        return resultLiveData
    }

    /**
     * **Should only be called if the user is logged in**
     *
     *  *Runs on the caller's thread/coroutine*
     * @return Observable [Result] of query for [UserInfo] for the currently logged in user
     */
    private fun getUserInfoBlocking() : UserInfo{
        initRealm()

        // find first userInfo matching user's id, throw an exception if none was found

        // get id of the logged in user
        val userId = ObjectId.from(realmApp.currentUser!!.identity)
        val result = mRealm
            .query<UserInfo>("_id == $0", userId)
            .first().find()
            ?: throw RealmObjectNotFoundException("UserInfo for the current user is not found!")
        // mRealm.close()

        return result
    }
    /**
     * Stores [UserInfo] about just registered user to [Realm]
     *
     * @param userInfo information about user to store
     * @return [Result] with inserted [UserInfo] on success or [Result] with [Throwable] explaining what went wrong on failure
     */
    private fun addUserInfo(userInfo: UserInfo){
        initRealm()

        mRealm.writeBlocking { this.copyToRealm(userInfo)}
        mRealm.close()
    }

    private fun initRealm(){
        if(!this::mRealm.isInitialized) {
            val config = SyncConfiguration
                .Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(UserInfo::class))
                .build()
            mRealm = Realm.open(config)
        }
    }

    override fun close() {
        if(this::mRealm.isInitialized && !mRealm.isClosed())
            mRealm.close()
    }
}