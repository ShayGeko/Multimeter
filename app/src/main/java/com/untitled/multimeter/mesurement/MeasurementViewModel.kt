package com.untitled.multimeter.mesurement

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MeasurementViewModel() : ViewModel() {
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
}