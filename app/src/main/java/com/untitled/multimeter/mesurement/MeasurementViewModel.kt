package com.untitled.multimeter.mesurement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jjoe64.graphview.series.DataPoint
import com.untitled.multimeter.data.model.*
import com.untitled.multimeter.data.source.ExperimentRepository
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.*

class MeasurementViewModel(private val userRepository: UserRepository, private val experimentRepository: ExperimentRepository) : ViewModel() {
    val measurementInput = MutableLiveData<Float>()
    val collectionStatus = MutableLiveData(false)

    lateinit var connection : Job

    private var isCollecting = false
    private var isConnectionOn = false

    /**
     * Mocks the connection to a measurement device, posts the data into [measurementInput]
     */
    fun mockConnection(){
        var volt = 0F;
        if(!isConnectionOn) {
            connection = CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    measurementInput.postValue(volt)
                    delay(500)
                    volt = (((1..10).random().toFloat()) / (11.0).toFloat()) * 10
                }
            }
            isConnectionOn = true;
        }
    }

    /**
     * Inverts the collecting status (collecting - not collecting)
     */
    fun changeCollectingStatus(){
        isCollecting = !isCollecting
        collectionStatus.value = isCollecting
    }

    override fun onCleared() {
        super.onCleared()

        connection.cancel()
        isConnectionOn = false;
    }

    /**
     * For adding a measurement to an experiment
     *
     * @param dataPoints - An ArrayList of DataPoints
     *
     * @return
     * LiveData<Result<Boolean>>, where Boolean is true if success
     */
    fun addMeasurementToExperiment(collectedData: ArrayList<DataPoint>, experimentObjectId: ObjectId): LiveData<Result<Boolean>> {
        return experimentRepository.addMeasurementToExperiment(collectedData, experimentObjectId)
    }

    fun getAllExperimentsForUser() : LiveData<Result<ArrayList<ExperimentModel>>> {
        return experimentRepository.getAllExperimentsForUser()
    }

    fun getExperiment(objectId: ObjectId): LiveData<Result<Experiment>> {
        return experimentRepository.getExperiment(objectId)
    }
}