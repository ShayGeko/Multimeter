package com.untitled.multimeter.experiments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.source.ExperimentRepository
import io.realm.kotlin.types.ObjectId

class ExperimentViewModel(private val repository: ExperimentRepository) : ViewModel() {

    fun getExperiment(experimentId: ObjectId) : LiveData<Result<Unit>> {
        return repository.getExperiment(experimentId) as MutableLiveData<Result<Unit>>
    }
}