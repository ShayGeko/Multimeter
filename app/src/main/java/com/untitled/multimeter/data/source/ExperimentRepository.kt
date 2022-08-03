package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jjoe64.graphview.series.DataPoint
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.MultimeterApp.Companion.REALM_PARTITION
import com.untitled.multimeter.MultimeterApp.Companion.getRealmInstance
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.*
import com.untitled.multimeter.data.model.ExperimentModel
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.exp

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
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val experimentList: RealmQuery<Experiment> = mRealm.query<Experiment>("_id == $0", experimentId)
                val experiment = experimentList.find()[0]
                return@runCatching experiment
            }.onSuccess { experiment ->
                result.postValue(Result.success(experiment))
            }.onFailure { error ->
                throw  error
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
                mRealm.writeBlocking {

                    //Inserting dummy data
                    //TODO: delete this
                    experiment.measurements = measurementsDummyData()

                    this.copyToRealm(experiment)

                }

                //Logs inserted experiments info
                /*
                Log.e("ExperimentRepository", "userInfo _id: "+experiment._id.toString())
                Log.e("ExperimentRepository", "userInfo title: "+experiment.title.toString())
                Log.e("ExperimentRepository", "userInfo collaborators: "+experiment.collaborators.toString())
                Log.e("ExperimentRepository", "userInfo comment: "+experiment.comment.toString())
                Log.e("ExperimentRepository", "userInfo measurements: "+experiment.measurements.toString())*/


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
            Log.d(APPLICATION_TAG, "copying experiment to realm")

            experiment.measurements = measurementsDummyData()
            var managedExperiment = copyToRealm(experiment)


            var cnt = 1
            for(receiver in receivers){
                Log.d(APPLICATION_TAG, "creating invitation #$cnt")
                val invite = CollaborationInvite(findLatest(managedExperiment)!!, findLatest(receiver)!!, findLatest(sender)!!)
                Log.d(APPLICATION_TAG, "storing invitation #$cnt")
                this.copyToRealm(invite)
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
        val measurements: ArrayList<MeasurementModel> = ArrayList()
        for (currentMeasurement in experiment.measurements) {
            val dataPoints = ArrayList<DataPoint>()
            for (currentDataPoints in ArrayList(currentMeasurement.dataPoints.toList())) {
                dataPoints.add(DataPoint(currentDataPoints.x, currentDataPoints.y))
            }
            measurements.add(MeasurementModel(currentMeasurement._id, currentMeasurement.user, dataPoints))
        }

        //Convert RealmInstant to Calendar Object
        val foundDate = Date(experiment.date.epochSeconds * 1000)
        val currentDate: Calendar = Calendar.getInstance()
        currentDate.time = foundDate

        return ExperimentModel(
            experiment._id,
            experiment.title,
            currentDate,
            experiment.comment,
            ArrayList(experiment.collaborators.toList()),
            measurements)
    }

    private fun measurementsDummyData(): RealmList<Measurement> {
        //TODO: delete this
        var result: RealmList<Measurement> = realmListOf()
        var dummyMeasurementDataPoint = realmListOf<MeasurementDataPoint>()
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 1.0; this.y = 6.2 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 2.0; this.y = 4.3 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 3.0; this.y = 5.0 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 4.0; this.y = 6.2 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 5.0; this.y = 10.0 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 6.0; this.y = 3.0 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 7.0; this.y = 9.4 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 8.0; this.y = 3.6 })
        var dummyMeasurement1 = Measurement().apply {
            this.user = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
            this.dataPoints = dummyMeasurementDataPoint
        }
        dummyMeasurementDataPoint = realmListOf<MeasurementDataPoint>()
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 6.0; this.y = 4.2 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 2.0; this.y = 5.3 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 4.0; this.y = 1.2 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 5.0; this.y = 4.0 })
        var dummyMeasurement2 = Measurement().apply {
            this.user = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
            this.dataPoints = dummyMeasurementDataPoint
        }
        result.add(dummyMeasurement1)
        result.add(dummyMeasurement2)
        return result
    }
}