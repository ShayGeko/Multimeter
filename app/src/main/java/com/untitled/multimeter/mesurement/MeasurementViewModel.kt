package com.untitled.multimeter.mesurement

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jjoe64.graphview.series.DataPoint
import com.untitled.multimeter.MainFragment
import com.untitled.multimeter.R
import com.untitled.multimeter.data.model.*
import com.untitled.multimeter.data.source.ExperimentRepository
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.SocketTimeoutException
import java.net.URL
import java.util.logging.XMLFormatter

class MeasurementViewModel(private val userRepository: UserRepository, private val experimentRepository: ExperimentRepository,
                           application: Application
                           ) : AndroidViewModel(application) {
    val measurementInput = MutableLiveData<DataPoint>()
    val collectionStatus = MutableLiveData(false)
    val sharedPreferences = application.getSharedPreferences("refresh rate", MODE_PRIVATE)
    var refreshrate:Float = 0.5F
    var delay:Long = (1000/sharedPreferences.getFloat("refresh rate", 0.5F)).toLong()
    var arraylist:ArrayList<DataPoint> = arrayListOf()
    var current_reading = MutableLiveData<DataPoint>()
    var x_value:Double = 0.0
    var connection_stat = MutableLiveData<Boolean>(true)

    lateinit var connection : Job

    var isCollecting = false
    private var isConnectionOn = false

    /**
     * Mocks the connection to a measurement device, posts the data into [measurementInput]
     */
//    fun mockConnection(){
//        var volt = 0F;
//        if(!isConnectionOn) {
//            connection = CoroutineScope(Dispatchers.IO).launch {
//                while (true) {
//                    measurementInput.postValue(volt)
//                    delay(500)
//                    volt = (((1..10).random().toFloat()) / (11.0).toFloat()) * 10
//                }
//            }
//            isConnectionOn = true;
//        }
//    }

    fun realConnection(){
        delay = (1000/sharedPreferences.getFloat("refresh rate", 0.5F)).toLong()
        var volt = 0F;
        if(!isConnectionOn) {
            connection = CoroutineScope(Dispatchers.IO).launch {
                while (true) {

                    var url: URL = URL("http://192.168.1.82/")

                    try{
                        val doc: Document = Jsoup
                            .connect("http://192.168.1.82/")
                            .timeout(10000)
                            .get()


                        val links = doc.select("h1")
                        val datapoint = DataPoint(x_value,(links[0].text()).toDouble())
                        measurementInput.postValue(datapoint)

                        if(isCollecting){
                            arraylist.add(datapoint)
                            x_value += ((delay)/1000.0).toDouble()
                        }
                        delay(delay)
                        Log.d("secondD",delay.toString())
                    }
                    catch (exception:Exception){
                       if(exception is SocketTimeoutException){
                           recall()
                           break
                       }
                        else{
                            connection_stat.postValue(false)
                        }
                    }
                }
            }
            isConnectionOn = true;
        }
    }

    suspend fun recall(){
        delay(500)
        withContext(Dispatchers.Main){
            isConnectionOn = false
            realConnection()
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

        if(this::connection.isInitialized){
            connection.cancel()
        }


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

    fun setRefreshRate(rate: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("refresh rate", rate)
        editor.apply()
    }
}