package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.REALM_PARTITION
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.*
import com.untitled.multimeter.data.model.ExperimentModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

/**
 * Repository for Experiments
 *
 */
class ExperimentRepository {
    private lateinit var mRealm : Realm

    /**
     * Tries to pull all experiments for the current user
     *
     * @returns
     * LiveData of the given experiment wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun getAllExperimentsForUser() : LiveData<Result<ArrayList<ExperimentModel>>> {
        val result = MutableLiveData<Result<ArrayList<ExperimentModel>>> ()
        CoroutineScope(Dispatchers.IO).launch {

            //Call private funtion to get the list of experiments for this user
            val userExperimentList = getAllExperimentObjectIdsForUser()
            Log.e("ExperimentRepository", "getAllExperimentObjectIdsForUser()" + userExperimentList.toString())

            //Set up realm config for query
            val config = SyncConfiguration.Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(Experiment::class))
                .build()
            mRealm = Realm.open(config)

            //Query for experiments that match with the ObjectIds in userExperimentList, add to userExperiments
            val userExperiments = ArrayList<ExperimentModel>()
            runCatching {

                mRealm.writeBlocking {

                    //For each experimentObjectId, get the corresponding experiment
                    if (userExperimentList != null && userExperimentList.isNotEmpty()) {
                        for (experimentObjectId in userExperimentList) {
                            val experimentQuery: RealmQuery<Experiment> =
                                this.query<Experiment>("_id == $0", experimentObjectId)
                            val experimentInfo = experimentQuery.find()[0]
                            userExperiments.add(experimentToExperimentDataClass(experimentInfo))
                        }
                    }
                    Log.e("ExperimentRepository", "userExperiments :" + userExperiments.toString())
                }
            }.onSuccess {
                result.postValue(Result.success(userExperiments))
            }.onFailure { exception: Throwable ->
                result.postValue(Result.failure(exception))
            }
            mRealm.close()
        }
        return result
    }

    /**
     * Tries to pull all experiments for the current user
     *
     * @returns
     * LiveData of the given experiment wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    private fun getAllExperimentObjectIdsForUser() :  ArrayList<ObjectId>?  {
        var userExperimentList: ArrayList<ObjectId> = ArrayList()
        runCatching {
            val config = SyncConfiguration.Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(
                UserInfo::class))
                .build()
            mRealm = Realm.open(config)
            val userId = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)

            //Get the current users entry
            val userQuery: RealmQuery<UserInfo> = mRealm.query<UserInfo>("_id == $0", userId)
            val userInfo = userQuery.find()[0]
            val userExperiments = userInfo.experiments

            //for each experiment ObjectId in userInfo, add to an array and return

            for (obj in userExperiments) {
                userExperimentList.add(obj)
            }
            return userExperimentList

        } // if no exception was thrown, propagate the successful Result
            .onSuccess { userExperimentList ->
                Log.d("getAllExperimentObjectIdsForUser", "Getting Experiments successful")
                Log.d("getAllExperimentObjectIdsForUser", "experiments: ${userExperimentList.toString()}")
                return userExperimentList
            } // otherwise, propagate the failed result
            .onFailure { exception : Throwable ->
                Log.e("getAllExperimentObjectIdsForUser", "Experiment add failed")
                Log.e("getAllExperimentObjectIdsForUser", exception.message.toString())
                return null
            }
        return null
    }

    /**
     * Tries to pull entry with the corresponding id
     *
     * @param experimentId - id
     * @returns
     * LiveData of the given experiment wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun getExperiment(experimentId: ObjectId) : LiveData<Result<Experiment>> {
        val result = MutableLiveData<Result<Experiment>> ()
        Log.e("getExperiment", "Get Data")
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("getExperiment", "Coroutine")
            runCatching {
                val configuration = SyncConfiguration.Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(Experiment::class))
                    .build()
                mRealm = Realm.open(configuration)
                val experimentList: RealmQuery<Experiment> = mRealm.query<Experiment>("experimentId == $0", experimentId)
                val x = experimentList.first()
                Log.e("getExperiment", x.toString())
            }
        }
        return result
    }

    /**
     * Adds experiment to the database
     *
     * @param experiment - id
     * @returns
     * LiveData of the given experiment wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun insertExperiment(experiment: Experiment): LiveData<Result<Boolean>>{
        val result = MutableLiveData<Result<Boolean>> ()
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val configuration = SyncConfiguration.Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(Experiment::class))
                    .build()
                mRealm = Realm.open(configuration)

                //Add Dummy Measurements
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 1.0; this.y = 6.2 })
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 2.0; this.y = 4.3 })
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 3.0; this.y = 5.0 })
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 4.0; this.y = 6.2 })
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 5.0; this.y = 10.0 })

                mRealm.writeBlocking {
                    this.copyToRealm(experiment)
                }
                Log.e("ExperimentRepository", "userInfo _id: "+experiment._id.toString())
                Log.e("ExperimentRepository", "userInfo title: "+experiment.title.toString())
                Log.e("ExperimentRepository", "userInfo collaborators: "+experiment.collaborators.toString())
                Log.e("ExperimentRepository", "userInfo comment: "+experiment.comment.toString())
                Log.e("ExperimentRepository", "userInfo measurements: "+experiment.measurements.toString())


            }
                .onSuccess { experiment ->
                    Log.d("insertExperiment", "Experiment insert succesful")
                    Log.d("insertExperiment", "new username: $experiment")
                    result.postValue(Result.success(true))
                }
                .onFailure { exception : Throwable ->
                    Log.e("insertExperiment", "Experiment insert failed")
                    Log.e("insertExperiment", exception.message.toString())
                    result.postValue(Result.failure(exception))
                }
            mRealm.close()
        }
        return result
    }

    /**
     * deletes experiment from the database
     *
     * @param experiment - id
     */
    fun deleteExperiment(objectId: ObjectId) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val configuration = SyncConfiguration.Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(Experiment::class))
                    .build()
                mRealm = Realm.open(configuration)

                mRealm.writeBlocking {
                    val experiment: Experiment = this.query<Experiment>("_id == $0", objectId).find().first()
                    delete(experiment)
                }


            }
                .onSuccess {
                    Log.d("deleteExperiment", "Experiment delete succesful")
                }
                .onFailure { exception : Throwable ->
                    Log.e("deleteExperiment", "Experiment delete failed")
                    Log.e("deleteExperiment", exception.message.toString())
                }
            mRealm.close()
        }
    }


    private fun experimentToExperimentDataClass(experiment: Experiment): ExperimentModel {
        return ExperimentModel(
            experiment._id,
            experiment.title,
            experiment.date,
            experiment.comment,
            ArrayList(experiment.collaborators.toList()),
            ArrayList(experiment.measurements.toList()))
    }
}