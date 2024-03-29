package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.MultimeterApp.Companion.getRealmInstance
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.CreateAccountModel
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.realm.RealmObjectNotFoundException
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.find
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

/**
 * Repository for users
 *
 */
class UserRepository {
    private lateinit var mRealm: Realm
    private var isRealmOpen = false

    /**
     * Attempts to login the user with the specified email and password
     *
     * @param email - email
     * @param password - password
     * @returns
     * LiveData of acquired User wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun login(email: String, password: String): LiveData<Result<UserInfo>> {
        val loginResult = MutableLiveData<Result<UserInfo>>()
        CoroutineScope(Dispatchers.IO).launch {
            // fancy try-catch block
            runCatching {
                // create the Credentials object from username and password
                val credentials = Credentials.emailPassword(email, password)

                // login, get the user with matched credentials or throw an exception
                // output will be wrapped in Result, because of runCatching{}
                realmApp.login(credentials)
                initRealm()
                mRealm.syncSession.downloadAllServerChanges(3.seconds)
                var found = false
                var cnt = 0
                lateinit var userInfo: UserInfo

                // wait for data to load from the server, keep querying for user data
                // timeout after 5 attempts
                while(!found && cnt < 5) {
                    ++cnt
                    try {
                        userInfo =  getCurrentUserInfoBlocking()
                        found = true
                    } catch (e: RealmObjectNotFoundException) {
                        delay(2.seconds)
                    }
                }

                return@runCatching userInfo
            } // if no exception was thrown, propagate the successful Result
                .onSuccess { user: UserInfo ->
                    loginResult.postValue(Result.success(user))

                } // otherwise, propagate the failed result
                .onFailure { exception: Throwable ->
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
    fun register(account: CreateAccountModel): LiveData<Result<Unit>> {
        val registerResult = MutableLiveData<Result<Unit>>()
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
                    this.experiments = realmListOf()
                }

                initRealm()
                mRealm.syncSession.downloadAllServerChanges(1.seconds)
                mRealm.writeBlocking {
                    this.copyToRealm(userInfo)
                    val managedUserInfo = query<UserInfo>("_id == $0", userInfo._id).first().find()!!

                    Log.d(APPLICATION_TAG, "userinfo insertion success, username : ${managedUserInfo.userName}")
                }

            } // if no exception was thrown, propagate the successful Result
                .onSuccess {
                    registerResult.postValue(Result.success(Unit))
                } // otherwise, propagate the failed result
                .onFailure { exception: Throwable ->
                    registerResult.postValue(Result.failure(exception))
                }
        }

        return registerResult
    }

//    /**
//     * get the invitation for the current user
//     *
//     * @param user - User
//     * @returns
//     * LiveData of the invitations for the user
//     */
//    fun getInvitations() : LiveData<Result<Unit>> {
//        val registerResult = MutableLiveData<Result<Unit>> ()
//        //query database
//        return registerResult
//    }


    /**
     * Attempts to add experiment to the user
     *
     * @param experiment - Experiment
     * @returns
     * LiveData of the invitations for the user
     */
    fun addExperimentToUser(experiment: Experiment): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        CoroutineScope(Dispatchers.IO).launch {
            initRealm()
            runCatching {
                mRealm.writeBlocking {

                    //Get the current users entry
                    val userId = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
                    val userQuery: RealmQuery<UserInfo> = this.query<UserInfo>("_id == $0", userId)
                    val userInfo = userQuery.first().find()

                    //Add Experiments to the user
                    userInfo!!.experiments.add(experiment._id)

                    return@writeBlocking userInfo
                }
            } // if no exception was thrown, propagate the successful Result
                .onSuccess { userInfo ->
                    Log.d("UserRepository", "Experiment add succesful")
                    Log.d("UserRepository", "new experiments: ${userInfo.experiments.toString()}")
                    result.postValue(Result.success(true))
                } // otherwise, propagate the failed result
                .onFailure { exception: Throwable ->
                    Log.e("UserRepository", "Experiment add failed")
                    Log.e("UserRepository", exception.message.toString())
                    result.postValue(Result.failure(exception))
                }
        }
        return result
    }

    /**
     * Attempts to remove experiment from the user
     *
     * @param objectId = ObjectId of the experiment
     */
    fun removeExperimentFromUser(objectId: ObjectId) {
        CoroutineScope(Dispatchers.IO).launch {
            initRealm()
            runCatching {

                mRealm.writeBlocking {

                    //Get the current users entry
                    val userId = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
                    val userQuery: RealmQuery<UserInfo> = this.query<UserInfo>("_id == $0", userId)
                    val userInfo = userQuery.find()[0]

                    //Add Experiments to the user
                    val currentExperiments = userInfo.experiments
                    currentExperiments.remove(objectId)
                    userInfo.experiments = currentExperiments
                }
            }
                .onSuccess {
                    Log.d("removeExperimentFromUser", "Experiment removal succesful")
                }
                .onFailure { exception: Throwable ->
                    Log.e("removeExperimentFromUser", "Experiment removal failed")
                    Log.e("removeExperimentFromUser", exception.message.toString())
                }
        }
    }

    /**
     * Get the users username
     *
     * @param objectId = ObjectId of the requested user
     */
    fun getUserName(objectId: ObjectId): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        CoroutineScope(Dispatchers.IO).launch {
            initRealm()
            runCatching {
                var userName: String? = null
                mRealm.writeBlocking {

                    //Get the current users entry
                    val userId = objectId
                    val userQuery: RealmQuery<UserInfo> = this.query<UserInfo>("_id == $0", userId)
                    val userInfo = userQuery.find()[0]

                    //Get userName of the user
                    userName = userInfo.userName
                }
                if (userName == null) {
                    throw RealmObjectNotFoundException("UserName for the current user not found!")
                }
                return@runCatching userName!!
            }
                .onSuccess { userName ->
                    Log.d("getUserName", "getUserName succesful")
                    result.postValue(Result.success(userName))
                }
                .onFailure { exception: Throwable ->
                    Log.e("getUserName", "getUserName failed")
                    Log.e("getUserName", exception.message.toString())
                }
        }
        return result
    }

    /**
     * **Should only be called if the user is logged in**
     *
     *  *Runs on the caller's thread/coroutine*
     * @return Observable [Result] of query for [UserInfo] for the currently logged in user
     */
     suspend fun getCurrentUserInfoBlocking() : UserInfo{
        initRealm()

        // find first userInfo matching user's id, throw an exception if none was found

        // get id of the logged in user
        val userId = ObjectId.from(realmApp.currentUser!!.identity)
        mRealm.syncSession.downloadAllServerChanges(2.seconds)
        Log.d(APPLICATION_TAG, "looking for userinfo for $userId")
        val result = mRealm
            .query<UserInfo>("_id == $0", userId)
            .first().find()
            ?: throw RealmObjectNotFoundException("UserInfo for the current user is not found!")

        return result
    }

    /**
     * **Should only be called if the user is logged in**
     *
     * Get user with matching email
     *
     *  *Runs on the caller's thread/coroutine*
     *  @param email email to match
     * @return [UserInfo] for the found  user
     * @throws NoSuchElementException if the user was not found
     */
    suspend fun getUserInfoByEmailAsync(email : String) : UserInfo{
        initRealm()
        mRealm.syncSession.downloadAllServerChanges(1.seconds)
        return mRealm
            .query<UserInfo>("email == $0", email)
            .first().find() ?: throw RealmObjectNotFoundException("User info not found for $email")
    }

    /**
     * **Should only be called if the user is logged in**
     *
     * Get user with matching email
     *
     *  *Runs on the caller's thread/coroutine*
     *  @param email email to match
     * @return Observable [Result] of query for [UserInfo] for the found  user
     */
    fun getUserInfoByEmail(email: String): LiveData<Result<UserInfo>> {
        val liveData = MutableLiveData<Result<UserInfo>>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = kotlin.runCatching {
                initRealm()

                return@runCatching mRealm.query<UserInfo>("email == $0", email)
                    .first().find() ?: throw RealmObjectNotFoundException()
            }

            liveData.postValue(result)

        }


        return liveData
    }

    /**
     * **Should only be called if the user is logged in**
     *
     * @return Observable [Result] of query for [UserInfo] for the currently logged in user
     */
    suspend fun getCurrentUserInfoAsync(): UserInfo {
        initRealm()

        val id = ObjectId.from(realmApp.currentUser!!.identity)

        mRealm.syncSession.downloadAllServerChanges(1.seconds)
        return mRealm.query<UserInfo>("_id == $0", id)
            .first().find() ?: throw RealmObjectNotFoundException("User not found for $id")
    }

    suspend fun addOrUpdateDeviceToken(userInfo: UserInfo, token: String) {
        initRealm()

        mRealm.write {
            val latestUserInfo = findLatest(userInfo)
            copyToRealm(latestUserInfo!!.apply { deviceToken = token })
        }
    }

    /**
     * Removes device registration token from current user
     * Does nothing if the user is not logged in
     */
    suspend fun removeDeviceRegistrationTokenFromCurrentUser() {
        initRealm()

        val user = realmApp.currentUser

        if (user != null && user.loggedIn) {
            val id = ObjectId.from(user.identity)
            mRealm.write {
                val userInfo = this.query<UserInfo>("_id == $0", id).first().find()

                userInfo?.deviceToken = null
            }
        }
    }

    /**
     * Stores [UserInfo] about just registered user to [Realm]
     *
     * @param userInfo information about user to store
     * @return [Result] with inserted [UserInfo] on success or [Result] with [Throwable] explaining what went wrong on failure
     */
    private fun addUserInfo(userInfo: UserInfo) {
        initRealm()

        Log.d(APPLICATION_TAG, "adding userinfo for ${userInfo._id}")
        mRealm.writeBlocking {
            this.copyToRealm(userInfo)
            val managedUserInfo = query<UserInfo>("_id == $0", userInfo._id).first().find()!!

            Log.d(APPLICATION_TAG, "userinfo insertion success, username : ${managedUserInfo.userName}")
        }
    }

    private fun initRealm(){
        if(!isRealmOpen) {
            mRealm = getRealmInstance()
            isRealmOpen = true
        }
    }

    /**
     * Attempts to update the user's username
     *
     * @param username = new username
     */
    fun updateUsername(username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            initRealm()
            runCatching {

                mRealm.writeBlocking {

                    //Get the current users entry
                    val userId = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
                    val userQuery: RealmQuery<UserInfo> = this.query<UserInfo>("_id == $0", userId)
                    val userInfo = userQuery.find()[0]

                    //Add Experiments to the user
                    userInfo.userName = username
                }
            }
                .onSuccess {
                    Log.d("updateUsername", "Username Update Success")
                }
                .onFailure { exception: Throwable ->
                    Log.e("updateUsername", "Username Update Failed")
                    Log.e("updateUsername", exception.message.toString())
                }
        }
    }

    /**
     * Attempts to update the user's email
     *
     * @param email = new email
     */
    fun updateEmail(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            initRealm()
            runCatching {

                mRealm.writeBlocking {

                    //Get the current users entry
                    val userId = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
                    val userQuery: RealmQuery<UserInfo> = this.query<UserInfo>("_id == $0", userId)
                    val userInfo = userQuery.find()[0]

                    //Add Experiments to the user
                    userInfo.email = email
                }
            }
                .onSuccess {
                    Log.d("updateEmail", "Email Update Success")
                }
                .onFailure { exception: Throwable ->
                    Log.e("updateEmail", "Email Update Failed")
                    Log.e("updateEmail", exception.message.toString())
                }
        }
    }
}