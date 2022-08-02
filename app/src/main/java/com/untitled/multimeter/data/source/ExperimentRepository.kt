package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.REALM_PARTITION
import com.untitled.multimeter.MultimeterApp.Companion.getRealmInstance
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.*
import com.untitled.multimeter.data.model.ExperimentModel
import io.realm.kotlin.ext.query
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
    private val mRealm = getRealmInstance()
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

            //Query for experiments that match with the ObjectIds in userExperimentList, add to userExperiments
            val userExperiments = ArrayList<ExperimentModel>()
            runCatching {

                mRealm.writeBlocking {

                    //For each experimentObjectId, get the corresponding experiment
                    if (userExperimentList != null && userExperimentList.isNotEmpty()) {
                        for (experimentObjectId in userExperimentList) {
                            val experimentQuery: RealmQuery<Experiment> = this.query<Experiment>("_id == $0", experimentObjectId)
                            val queryResult = experimentQuery.find()
                            if (queryResult.isNotEmpty()) {
                                val experimentInfo = queryResult[0]
                                userExperiments.add(experimentToExperimentDataClass(experimentInfo))
                            }
                            else {
                                Log.e("ExperimentRepository", "ObjectId "+experimentObjectId+" does not exist")
                            }
                        }
                    }
                    Log.e("ExperimentRepository", "userExperiments :" + userExperiments.toString())
                }
            }.onSuccess {
                result.postValue(Result.success(userExperiments))
            }.onFailure { exception: Throwable ->
                result.postValue(Result.failure(exception))
            }
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

        }
            .onFailure { exception : Throwable ->
                Log.e("getAllExperimentObjectIdsForUser", "Experiments get for user failed")
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
     * TODO: Fix to use receivers as collaborators in experiment
     *
     * @param experiment - id
     * @returns
     * LiveData of the given experiment wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun insertExperiment(experiment: Experiment, receivers : ArrayList<UserInfo>): LiveData<Result<Boolean>>{
        val result = MutableLiveData<Result<Boolean>> ()
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                //Add Dummy Measurements
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 1.0; this.y = 6.2 })
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 2.0; this.y = 4.3 })
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 3.0; this.y = 5.0 })
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 4.0; this.y = 6.2 })
                //experiment.measurements.add(RealmDataPoint().apply { this.x = 5.0; this.y = 10.0 }


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
        }
        return result
    }

    suspend fun insertExperimentAsync(experiment: Experiment, sender: UserInfo, receivers: ArrayList<UserInfo>) {
        mRealm.write {
            this.copyToRealm(experiment)

            for(receiver in receivers){
                copyToRealm(CollaborationInvite(experiment, receiver, sender))
            }

        }
    }
    /**
     * deletes experiment from the database
     *
     * @param experiment - id
     */
    fun deleteExperiment(objectId: ObjectId) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
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