package com.untitled.multimeter.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.data.model.Experiment
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Repository for Experiments
 *
 */
class ExperimentRepository {

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
            Log.e("ExperimentRepository","Get Data")
            //val configuration = RealmConfiguration.create(schema = setOf(Experiment::class))
            //val realm = Realm.open(configuration)
            //val experimentList: RealmQuery<Experiment> = realm.query<Experiment>("experimentId == $0", experimentId)
            //val x = experimentList.first()
        }
        return result
    }
}