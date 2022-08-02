package com.untitled.multimeter.createexperiment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.source.ExperimentRepository
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.types.ObjectId

class CreateExperimentViewModel(private val userRepository: UserRepository, private val experimentRepository: ExperimentRepository) : ViewModel() {

    /**
     * Attempts to insert experiment into database
     *
     * @param experiment - Experiment
     * @returns
     *
     */
    fun insertExperiment(experiment: Experiment): LiveData<Result<Boolean>> {
        return experimentRepository.insertExperiment(experiment)
    }

    /**
     * Add Experiment to the user
     *
     * @param experiment - Experiment
     * @returns
     *
     */
    fun addExperimentToUser(experiment: Experiment): LiveData<Result<Boolean>> {
        return userRepository.addExperimentToUser(experiment)
    }
}