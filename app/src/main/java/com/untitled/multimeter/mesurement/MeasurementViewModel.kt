package com.untitled.multimeter.mesurement

import android.widget.TextView
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
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.util.logging.XMLFormatter

class MeasurementViewModel(private val userRepository: UserRepository, private val experimentRepository: ExperimentRepository) : ViewModel() {
    val measurementInput = MutableLiveData<DataPoint>()
    val collectionStatus = MutableLiveData(false)
    var refreshrate:Int = 30
    var delay:Long = (1000/refreshrate).toLong()
    var arraylist:ArrayList<DataPoint> = arrayListOf()
    var current_reading = MutableLiveData<DataPoint>()
    var x_value:Double = 0.0
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
        var volt = 0F;
        if(!isConnectionOn) {
            connection = CoroutineScope(Dispatchers.IO).launch {

                while (true) {
                    var url: URL = URL("http://192.168.4.1/")

                    try{
                        val doc: Document = Jsoup
                            .connect("http://192.168.4.1/")
                            .timeout(1000)
                            .get()

                        val links = doc.select("h1")

                        val datapoint = DataPoint(x_value,(links[0].text()).toDouble())

                        measurementInput.postValue(datapoint)

                        if(isCollecting){
                            arraylist.add(datapoint)
                            x_value += (delay/1000.0)
                        }
                        delay(delay)


                    }
                    catch (exception:Exception){
                        recall()
                        break

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