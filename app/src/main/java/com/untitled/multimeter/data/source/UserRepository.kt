package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.REALM_PARTITION
import com.untitled.multimeter.MultimeterApp.Companion.getRealmInstance
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.CreateAccountModel
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.realm.RealmObjectNotFoundException
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Repository for users
 *
 */
class UserRepository {
    private lateinit var mRealm : Realm
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
     * get the invitation for the current user
     *
     * @param user - User
     * @returns
     * LiveData of the invitations for the user
     */
    fun getInvitations(user: User) : LiveData<Result<Unit>> {
        val registerResult = MutableLiveData<Result<Unit>> ()
        //query database
        return registerResult
    }

    /**
     * Attempts to add experiment to the user
     *
     * @param experiment - Experiment
     * @returns
     * LiveData of the invitations for the user
     */
    fun addExperimentToUser(experiment: Experiment): LiveData<Result<Boolean>>{
        val result = MutableLiveData<Result<Boolean>> ()
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
                    currentExperiments.add(experiment._id)
                    userInfo.experiments = currentExperiments

                    //Logs the user
                    /*
                    Log.e("UserRepository", "userInfo _id: "+userInfo._id.toString())
                    Log.e("UserRepository", "userInfo username: "+userInfo.userName.toString())
                    Log.e("UserRepository", "userInfo email: "+userInfo.email.toString())
                    Log.e("UserRepository", "userInfo experiment: "+userInfo.experiments.toString())*/

                    return@writeBlocking userInfo
                }
            } // if no exception was thrown, propagate the successful Result
                .onSuccess { userInfo ->
                    Log.d("UserRepository", "Experiment add succesful")
                    Log.d("UserRepository", "new experiments: ${userInfo!!.experiments.toString()}")
                    result.postValue(Result.success(true))
                } // otherwise, propagate the failed result
                .onFailure { exception : Throwable ->
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
                .onFailure { exception : Throwable ->
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
    fun getUserName(objectId: ObjectId) : LiveData<Result<String>>{
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
     * Stores [UserInfo] about just registered user to [Realm
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

        return result
    }

    /**
     * **Should only be called if the user is logged in**
     *
     * @return Observable [Result] of query for [UserInfo] for the currently logged in user
     */
    private fun getUserInfoWithQueryFlow() : LiveData<UserInfo> {
        initRealm()

        val resultLiveData = MutableLiveData<UserInfo>()

        val id = ObjectId.from(realmApp.currentUser!!.identity)

        val query = mRealm.query<UserInfo>("_id == $0", id).first()

        CoroutineScope(Dispatchers.IO).launch {
            query.asFlow().collect{
                result ->
                when(result){
                    is InitialObject<UserInfo>,
                    is UpdatedObject<UserInfo> -> {
                        resultLiveData.postValue(result.obj)
                        this.cancel()
                    }
                    else -> {}
                }
            }
        }
        return resultLiveData
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
    }

    private fun initRealm(){
        if(!isRealmOpen) {
            mRealm = getRealmInstance()
            isRealmOpen = true
        }
    }
}