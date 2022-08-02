package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.MultimeterApp.Companion.REALM_PARTITION
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.CreateAccountModel
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.UserInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.RealmQuery
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
     * Attempts to register the user with the specified email and password
     *
     * @param user - email
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
    fun addExperimentToUser(experiment: Experiment){
        CoroutineScope(Dispatchers.IO).launch {

            runCatching {
                val config = SyncConfiguration.Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(UserInfo::class))
                    .build()
                mRealm = Realm.open(config)

                mRealm.writeBlocking {

                    //Get the current users entry
                    val userId = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
                    val userQuery: RealmQuery<UserInfo> = this.query<UserInfo>("_id == $0", userId)
                    val userInfo = userQuery.find()[0]
                    Log.e("UserRepository", "userInfo _id: "+userInfo._id.toString())
                    Log.e("UserRepository", "userInfo username: "+userInfo.userName.toString())
                    Log.e("UserRepository", "userInfo email: "+userInfo.email.toString())
                    Log.e("UserRepository", "userInfo experiment: "+userInfo.experiments.toString())

                    //Add Experiments to the user
                    val currentExperiments = userInfo.experiments
                    currentExperiments.add(experiment._id)
                    userInfo.experiments = currentExperiments

                    Log.e("UserRepository", "userInfo -> AFTER APPLY")
                    Log.e("UserRepository", "userInfo _id: "+userInfo._id.toString())
                    Log.e("UserRepository", "userInfo username: "+userInfo.userName.toString())
                    Log.e("UserRepository", "userInfo email: "+userInfo.email.toString())
                    Log.e("UserRepository", "userInfo experiment: "+userInfo.experiments.toString())

                    return@writeBlocking userInfo
                }
            } // if no exception was thrown, propagate the successful Result
                .onSuccess { userInfo ->
                    Log.d("UserRepository", "Experiment add succesful")
                    Log.d("UserRepository", "new experiments: ${userInfo!!.experiments.toString()}")
                } // otherwise, propagate the failed result
                .onFailure { exception : Throwable ->
                    Log.e("UserRepository", "Experiment add failed")
                    Log.e("UserRepository", exception.message.toString())
                }

            mRealm.close()

            // fancy try-catch block

        }
    }

    /**
     * Attempts to remove experiment from the user
     *
     * @param objectId = ObjectId of the experiment
     */
    fun removeExperimentFromUser(objectId: ObjectId) {
        CoroutineScope(Dispatchers.IO).launch {
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
            mRealm.close()
        }
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