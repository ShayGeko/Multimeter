package com.untitled.multimeter.connection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class ConnectionViewModel() : ViewModel() {
    val connectionStatus = MutableLiveData(false)


    private var isConnected = false

    /**
     * Inverts the collecting status (collecting - not collecting)
     */
    fun changeConnectionStatus(){
        CoroutineScope(Dispatchers.IO).launch{
            delay(500)
            isConnected = !isConnected
            connectionStatus.postValue(isConnected)
        }
    }
}